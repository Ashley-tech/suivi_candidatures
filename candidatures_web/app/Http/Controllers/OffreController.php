<?php

namespace App\Http\Controllers;
use App\Models\Offre;
use App\Models\Candidature;

use Illuminate\Http\Request;

class OffreController extends Controller
{
    //
    public function ajouterOffre(Request $request) {
        $request->validate([
            'titre' => 'required|string|max:255',
            'type' => 'required|string|max:255',
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

        $offre = Offre::create($request->all());

        $request->validate([
            'compte' => 'required|integer|exists:compte,id',  // ID du compte associé
            'offre' => 'required|integer|exists:offre,id',    // ID de l'offre associée
            'cv' => 'required|integer|exists:cv,id',          // ID du CV associé
        ]);

        $candidature = Candidature::create([
            'compte' => $request->compte,
            'offre' => $offre->id,
            'cv' => $request->cv,
            'date_candidature' => $request->date_,
        ]);
    }   
}
