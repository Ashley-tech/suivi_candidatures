<?php

namespace App\Http\Controllers;
use App\Models\CV;

use Illuminate\Http\Request;

use Smalot\PdfParser\Parser;

class CVController extends Controller
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

    public function modifyFile(Request $request, int $id) {
        $cv = CV::findOrFail($id);

        $request->validate([
            'cv' => 'required|file|mimes:pdf,doc,docx|max:2048',  // Validation du fichier
        ]);

        $file = $request->file('cv');

        $cv->update([
            'nom' => $file->getClientOriginalName(),
            'contenu' => file_get_contents($file->getRealPath()),
            'mime_type' => $file->getMimeType(),
            'date_upload' => now(),
        ]);

        return response()->json([
            'message' => 'CV updated successfully',
            'cv_id' => $cv->id
        ], 200);
    }

    public function enregistrerFile(Request $request) {
        //dd('UPLOAD HIT');
        $request->validate([
            'cv' => 'required|file|mimes:pdf,doc,docx|max:2048',  // Validation du fichier
            'compte' => 'required|integer|exists:compte,id',  // ID du compte associé
        ]);

        $file = $request->file('cv');
        //$path = $file->store('cvs');

        // Créer l'enregistrement en BDD
        $cv = CV::create([
            'nom' => $file->getClientOriginalName(),
            'contenu' => file_get_contents($file->getRealPath()), // 👈 stocké en BDD
            'mime_type' => $file->getMimeType(), // 👈 important
            'compte' => $request->compte,
            'date_upload' => now(),
            'visible' => true,
        ]);

        return response()->json([
            'message' => 'CV uploaded and saved successfully',
            //'path' => $path,
            'cv_id' => $cv->id
        ], 200);
    }

    public function telecharger(int $id) {
        $cv = CV::findOrFail($id);

        return response($cv->contenu)
            ->header('Content-Type', 'application/pdf') // adapter selon type
            ->header('Content-Disposition', 'attachment; filename="'.$cv->nom.'"');
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

    public function delete(int $id) {
        $cv = CV::findOrFail($id);
        $cv->delete();

        return response()->json(['message' => 'CV deleted successfully'], 200);
    }
}
