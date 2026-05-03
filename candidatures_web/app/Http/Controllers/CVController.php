<?php

namespace App\Http\Controllers;
use App\Models\CV;
use App\Http\Controllers\CandidatureController;

use Illuminate\Http\Request;

use Smalot\PdfParser\Parser;
use PhpOffice\PhpWord\IOFactory;

class CVController extends Controller
{

    public function cvextracted(mixed $file) {
        $mimeType = $file->getMimeType();
        $filePath = $file->getRealPath();

        $texte = '';

        // 🔹 PDF
        if (str_contains($mimeType, 'pdf')) {
            $parser = new Parser();
            $pdf = $parser->parseFile($filePath);
            $texte = $pdf->getText();
        }

        // 🔹 DOCX
        elseif (
            str_contains($mimeType, 'word') ||
            str_contains($mimeType, 'officedocument')
        ) {
            $phpWord = IOFactory::load($filePath);
            foreach ($phpWord->getSections() as $section) {
                foreach ($section->getElements() as $element) {
                    if (method_exists($element, 'getText')) {
                        $texte .= $element->getText() . ' ';
                    }
                }
            }
        }

        // 🔹 ODT
        elseif (str_contains($mimeType, 'opendocument')) {
            $zip = new \ZipArchive;

            if ($zip->open($filePath) === TRUE) {
                $content = $zip->getFromName('content.xml');
                $zip->close();

                $texte = strip_tags($content);
            }
        }

        // 🔹 fallback (sécurité)
        else {
            return response()->json([
                'message' => 'Format non supporté pour extraction'
            ], 400);
        }

        // Nettoyage
        $texte = strtolower($texte);
        $texte = str_replace(["\n", "\r"], ' ', $texte);
        $texte = preg_replace('/\s+/', ' ', $texte);

        return $texte;
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
            'texte_extrait' => $this->cvextracted($file),
        ]);

        $candidatures = $cv->candidatures()->get();
        foreach ($candidatures as $candidature) {
            $candidature->score_matching = null; // reset score
            $candidature->save();
        }

        return response()->json([
            'message' => 'CV updated successfully',
            'cv_id' => $cv->id
        ], 200);
    }

    public function enregistrerFile(Request $request) {

        $request->validate([
            'cv' => 'required|file|mimes:pdf,doc,docx,odt|max:2048',
            'compte' => 'required|integer|exists:compte,id',
        ]);

        $file = $request->file('cv');

        // Enregistrement
        $cv = CV::create([
            'nom' => $file->getClientOriginalName(),
            'contenu' => file_get_contents($file->getRealPath()),
            'mime_type' => $file->getMimeType(),
            'compte' => $request->compte,
            'date_upload' => now(),
            'visible' => true,
            'texte_extrait' => $this->cvextracted($file),
        ]);

        return response()->json([
            'message' => 'CV uploaded and saved successfully',
            'cv_id' => $cv->id
        ], 200);
    }

    public function telecharger(int $id) {
        $cv = CV::findOrFail($id);

        return response($cv->contenu)
            ->header('Content-Type', 'application/pdf') // adapter selon type
            ->header('Content-Disposition', 'attachment; filename="'.$cv->nom.'"');
    }

    public function delete(int $id) {
        $cv = CV::findOrFail($id);
        $cv->delete();

        return response()->json(['message' => 'CV deleted successfully'], 200);
    }
}
