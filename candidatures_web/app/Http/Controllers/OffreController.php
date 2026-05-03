<?php

namespace App\Http\Controllers;
use App\Models\Offre;
use App\Models\Candidature;
use App\Http\Controllers\CandidatureController;

use Illuminate\Http\Request;

class OffreController extends Controller
{
    //
    public function ajouterOffre(Request $request) {
        $request->validate([
            'titre' => 'required|string|max:255',
            'type' => 'string|max:255',
            'description' => 'string',
            'nom_entreprise' => 'string|max:255',
            'adresse_entreprise' => 'string|max:255',
            'adresse_comp_entreprise' => 'string|max:255',
            'cp_entreprise' => 'string|max:20',
            'ville_entreprise' => 'string|max:255',
            'pays_entreprise' => 'string|max:255',
            'nom_recruteur' => 'string|max:255',
            'prenom_recruteur' => 'string|max:255',
            'email_entreprise' => 'email|max:255',
            'tel_entreprise' => 'string|max:20',
            'periode' => 'string|max:255',
            'salaire_min' => 'numeric',
            'salaire_max' => 'numeric|gte:salaire_min',
            'date_publication' => 'date',
        ]);
        if (!isset($request->titre)) {
            return response()->json(['message' => 'Le champ titre est requis', 'success' => false], 400);
        }
        if (!isset($request->date_publication)) {
            $request->merge(['date_publication' => now()]);
        }

        $offre = Offre::create($request->all());

        return response()->json([
            'message' => 'Offre created successfully',
            'success' => true,
            'offre_id' => $offre->id
        ], 200);

        /*$request->validate([
            'compte' => 'required|integer|exists:compte,id',  // ID du compte associé
            'offre' => 'required|integer|exists:offre,id',    // ID de l'offre associée
            'cv' => 'required|integer|exists:cv,id',          // ID du CV associé
        ]);

        $candidature = Candidature::create([
            'compte' => $request->compte,
            'offre' => $offre->id,
            'cv' => $request->cv,
            'date_candidature' => $request->date ?? now(),
        ]);*/
    }

    public function getOffres() {
        $offres = Offre::all();
        return response()->json($offres, 200);
    }

    public function getOffre(int $id) {
        $offre = Offre::findOrFail($id);
        return response()->json($offre, 200);
    }

    public function deleteOffre(int $id) {
        $offre = Offre::findOrFail($id);
        $offre->delete();
        $candidaturesCorrespondantes = Candidature::where('offre', $id)->get();
        foreach ($candidaturesCorrespondantes as $candidature) {
            $candidature->delete();
        }
        return response()->json(['message' => 'Offre deleted successfully', 'success' => true], 200);
    }

    public function updateOffre(int $id, Request $request) {
        $offre = Offre::findOrFail($id);

        $request->validate([
            'titre' => 'required|string|max:255',
            'type' => 'string|max:255',
            'description' => 'string',
            'nom_entreprise' => 'string|max:255',
            'adresse_entreprise' => 'string|max:255',
            'adresse_comp_entreprise' => 'string|max:255',
            'cp_entreprise' => 'string|max:20',
            'ville_entreprise' => 'string|max:255',
            'pays_entreprise' => 'string|max:255',
            'nom_recruteur' => 'string|max:255',
            'prenom_recruteur' => 'string|max:255',
            'email_entreprise' => 'email|max:255',
            'tel_entreprise' => 'string|max:20',
            'periode' => 'string|max:255',
            'salaire_min' => 'numeric',
            'salaire_max' => 'numeric|gte:salaire_min',
            'date_publication' => 'date',
        ]);

        $offre->update($request->all());

        return response()->json([
            'message' => 'Offre updated successfully',
            'success' => true,
            'offre' => $offre,
            'id' => $offre->id
        ], 200);
    }
}
