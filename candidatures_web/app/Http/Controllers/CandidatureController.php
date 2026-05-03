<?php

namespace App\Http\Controllers;
use App\Models\Candidature;

use Illuminate\Http\Request;

use Smalot\PdfParser\Parser;

class CandidatureController extends Controller
{
    private function cleanText(mixed $text) {
        $text = strtolower($text);
        $text = preg_replace('/[^a-z0-9\s]/', '', $text);

        $stopWords = ['le', 'la', 'les', 'de', 'du', 'des', 'un', 'une', 'et', 'ou'];

        $words = array_filter(explode(' ', $text), function ($word) use ($stopWords) {
            return !in_array($word, $stopWords) && strlen($word) > 2;
        });

        return $words;
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

    public function saveScore(int $candidatureId) {
        $candidature = Candidature::findOrFail($candidatureId);

        $cv = $candidature->cv()->first();
        $offre = $candidature->offre()->first();

        if (!$cv || !$offre) {
            return response()->json([
                'message' => 'CV or offre not found for this candidature',
            ], 404);
        }

        // 1. Extraire texte
        $cvText = $cv->texte_extrait;
        $offreText = strtolower($offre->description);

        // 2. Nettoyer
        $cvWords = $this->cleanText($cvText);
        $offreWords = $this->cleanText($offreText);

        // 3. Score
        $score = $this->computeScore($cvWords, $offreWords);

        // 4. Sauvegarde
        $candidature->score_matching = $score;
        $candidature->save();

        return response()->json([
            'score' => $score
        ]);
    }

    public function deleteCandidature(int $id) {
        $candidature = Candidature::findOrFail($id);
        $candidature->delete();

        return response()->json([
            'message' => 'Candidature deleted successfully',
            'success' => true
        ], 200);
    }
}
