<?php

namespace App\Http\Controllers;
use App\Models\CV;

use Illuminate\Http\Request;

class CVController extends Controller
{
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

        /*$cv = new CV();
        $cv->nom = $file->getClientOriginalName();
        $cv->contenu = file_get_contents($file->getRealPath());
        $cv->mime_type = $file->getMimeType();
        $cv->compte = $request->compte;
        $cv->date_upload = now();
        $cv->visible = true;
        $cv->save();

        dd($cv);*/

        echo $cv;

        return response()->json([
            'message' => 'CV uploaded and saved successfully',
            //'path' => $path,
            'cv_id' => $cv->id
        ], 200);
    }

    public function telecharger($id) {
        $cv = CV::findOrFail($id);

        return response($cv->contenu)
            ->header('Content-Type', 'application/pdf') // adapter selon type
            ->header('Content-Disposition', 'attachment; filename="'.$cv->nom.'"');
    }
}
