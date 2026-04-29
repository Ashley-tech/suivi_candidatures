<?php

namespace App\Http\Controllers;
use App\Models\Candidature;

use Illuminate\Http\Request;

use Smalot\PdfParser\Parser;

class CandidatureController extends Controller
{
    //
    public function enregistrerCandidature(Request $request) {
        $request->validate([
            'compte' => 'required|integer|exists:compte,id',  // ID du compte associé
            'offre' => 'required|integer|exists:offre,id',    // ID de l'offre associée
            'cv' => 'required|integer|exists:cv,id',          // ID du CV associé
        ]);

        $candidature = Candidature::create([
            'compte' => $request->compte,
            'offre' => $request->offre,
            'cv' => $request->cv,
            'date_candidature' => now(),
        ]);

        //$this->saveScore($candidature->id); //Optionnel si on ne veut pas tester le matching à chaque création de candidature

        return response()->json([
            'message' => 'Candidature created successfully',
            'candidature_id' => $candidature->id
        ], 200);
    }

    public function updateStatut(int $candidatureId, string $statut) {
        $candidature = Candidature::findOrFail($candidatureId);
        $candidature->statut = $statut;
        $candidature->save();

        return response()->json([
            'message' => 'Statut updated successfully',
            'candidature' => $candidature
        ], 200);
    }

    public function saveScore(int $candidatureId) {
        $candidature = Candidature::findOrFail($candidatureId);

        $cv = $candidature->cvRelation;
        $offre = $candidature->offreRelation;

        // 1. Extraire texte
        $cvText = $this->extractTextFromCV($cv);
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
}
