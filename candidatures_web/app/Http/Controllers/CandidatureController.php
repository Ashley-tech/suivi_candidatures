<?php

namespace App\Http\Controllers;
use App\Models\Candidature;

use Illuminate\Http\Request;

use Smalot\PdfParser\Parser;

class CandidatureController extends Controller
{
    private function cleanText($text) {
        $text = strtolower($text);
        $text = preg_replace('/[^a-z0-9\s]/', ' ', $text);
        $text = preg_replace('/\s+/', ' ', $text);

        $stopWords = [
            'le','la','les','de','du','des','un','une','et','ou','en','dans',
            'pour','with','the','and','or','to','a','an'
        ];

        $words = array_filter(explode(' ', $text), function ($word) use ($stopWords) {
            return strlen($word) > 2 && !in_array($word, $stopWords);
        });

        $words = array_map([$this, 'normalizeWord'], $words);

        return array_values($words);
    }

    private function getWeights() {
        return [
            'php' => 5,
            'laravel' => 5,
            'symfony' => 4,
            'javascript' => 4,
            'react' => 4,
            'sql' => 4,
            'docker' => 3,
            'git' => 2,
        ];
    }

    private function computeTfIdfScore($cvWords, $offreWords) {

        $cvFreq = array_count_values($cvWords);

        $score = 0;
        $maxScore = 0;

        foreach ($offreWords as $word) {

            // 👉 ICI on définit le poids du mot
            if (isset($weights[$word])) {
                $score += $weights[$word];
                $maxScore += $weights[$word];
            } else {
                $maxScore += 1; // poids par défaut
            }

            // 👉 on vérifie si le CV contient le mot
            if (in_array($word, $cvWords)) {
                $score += isset($weights[$word]) ? $weights[$word] : 1;
            }
        }

        return $maxScore > 0
            ? round(($score / $maxScore) * 100, 2)
            : 0;
    }

    private function computeScore(mixed $cvWords, mixed $offreWords) {
        $common = array_intersect($cvWords, $offreWords);

        if (count($offreWords) === 0) return 0;

        return round((count($common) / count($offreWords)) * 100, 2);
    }

    public function enregistrerCandidature(Request $request) {
        $request->validate([
            'compte' => 'required|integer|exists:compte,id',  // ID du compte associé
            'offre' => 'required|integer|exists:offre,id',    // ID de l'offre associée
            'cv' => 'required|integer|exists:cv,id',          // ID du CV associé
            'date' => 'date',                                  // Date de candidature (optionnelle)
        ]);

        $candidature = Candidature::create([
            'compte' => $request->compte,
            'offre' => $request->offre,
            'cv' => $request->cv,
            'date_candidature' => $request->date ?? now(),
        ]);

        //$this->saveScore($candidature->id); //Optionnel si on ne veut pas tester le matching à chaque création de candidature

        return response()->json([
            'message' => 'Candidature created successfully',
            'candidature' => $candidature,
            'id' => $candidature->id
        ], 200);
    }

    public function updateStatut(Request $request, int $candidatureId) {
        $request->validate([
            'statut' => 'required|string|max:255',
        ]);

        $candidature = Candidature::findOrFail($candidatureId);
        $candidature->statut = $request->input('statut');
        $candidature->save();

        return response()->json([
            'message' => 'Statut updated successfully',
            'candidature' => $candidature
        ], 200);
    }

    private function extractTextFromCV(mixed $cv) {
        $parser = new Parser();

        try {
            $pdf = $parser->parseContent($cv->contenu);
            return strtolower($pdf->getText());
        } catch (\Exception $e) {
            return '';
        }
    }

    private function getWordWeight($word, $cvWords, $offreWords) {
        $cvFreq = array_count_values($cvWords);

        // plus un mot est répété dans le CV → plus il est important
        $tf = $cvFreq[$word] ?? 1;

        return min($tf, 3); // limite pour éviter explosion
    }

    private function computeKeywordMatch($cvWords, $offreWords) {

        $cvFreq = array_count_values($cvWords);

        $score = 0;
        $maxScore = 0;

        foreach ($offreWords as $word) {

            // poids dynamique basé sur le CV
            $weight = min($cvFreq[$word] ?? 1, 3);

            $maxScore += $weight;

            if (isset($cvFreq[$word])) {
                $score += $weight;
            }
        }

        return $maxScore > 0
            ? round(($score / $maxScore) * 100, 2)
            : 0;
    }

    private function normalizeWord($word) {
        $map = [
            'dev' => 'developpeur',
            'developer' => 'developpeur',
            'programmeur' => 'developpeur',
            'js' => 'javascript',
            'nodejs' => 'javascript',
            'node' => 'javascript',
            'php8' => 'php',
            'php7' => 'php',
            'laravel framework' => 'laravel',
        ];

        return $map[$word] ?? $word;
    }

    private function getSkillsDictionary() {
        return [
            'php', 'laravel', 'symfony',
            'javascript', 'react', 'vue', 'node',
            'sql', 'mysql', 'postgresql',
            'docker', 'git', 'linux',
            'html', 'css'
        ];
    }

    private function extractKeywords($words) {

        $freq = array_count_values($words);

        // garder seulement les mots importants (fréquence >= 1 ici simple)
        return array_keys($freq);
    }

    private function extractSkills($words) {
        $skills = $this->getSkillsDictionary();
        return array_values(array_intersect($words, $skills));
    }

    private function extractOfferSkills($words) {
        $skills = $this->getSkillsDictionary();
        return array_values(array_intersect($words, $skills));
    }

    public function saveScore(int $candidatureId) {
        $candidature = Candidature::findOrFail($candidatureId);

        $cv = $candidature->cv()->first();
        $offre = $candidature->offre()->first();

        if (!$cv || !$offre) {
            return response()->json(['message' => 'missing data'], 404);
        }

        // 1. TEXTES
        $cvText = $cv->texte_extrait;
        $offreText = strtolower($offre->titre . ' ' . $offre->description);

        // 2. CLEAN
        $cvWords = $this->cleanText($cvText);
        $offreWords = $this->cleanText($offreText);

        // 3. MATCHING GÉNÉRAL
        $baseScore = $this->computeKeywordMatch($cvWords, $offreWords);

        // 4. BONUS TITRE (universel)
        $bonus = 0;

        if (str_contains($cvText, strtolower($offre->titre))) {
            $bonus += 15;
        }

        // 5. SCORE FINAL
        $score = $baseScore + $bonus;
        $score = min(100, round($score, 2));

        // 6. SAVE
        $candidature->score_matching = $score;
        $candidature->save();

        return response()->json([
            'score' => $score
        ]);
    }

    public function delete(int $id) {
        $candidature = Candidature::findOrFail($id);
        $candidature->delete();

        return response()->json([
            'message' => 'Candidature deleted successfully',
            'success' => true
        ], 200);
    }
}
