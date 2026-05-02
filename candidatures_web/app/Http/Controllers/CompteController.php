<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Compte;

class CompteController extends Controller
{
    public function login(Request $request){
        $request->validate([
            'email' => 'required|email',
            'mdp' => 'required|string',
        ]);

        $compte = Compte::where('email', $request->email)->first();
        if ($compte && password_verify($request->mdp, $compte->mdp_crypted)) {
            return response()->json(['message' => 'Login successful','success' => true, 'compte' => $compte], 200);
        } else {
            return response()->json(['message' => 'Invalid credentials','success' => false], 401);
        }
    }

    public function delete(int $id) {
        $compte = Compte::findOrFail($id);
        $compte->delete();

        return response()->json(['message' => 'Compte deleted successfully','success' => true], 200);
    }

    public function updatePwd(int $id, Request $request) {
        $compte = Compte::findOrFail($id);

        $validated = $request->validate([
            'mdp' => 'required|string',
        ]);

        $compte->mdp_crypted = (new Compte())->hashPwd($validated['mdp']);
        $compte->mdp = $validated['mdp']; // Stockez le mot de passe en clair pour la réponse (optionnel, à ne pas faire en production)
        $compte->save();

        return response()->json(['message' => 'Password updated successfully','success' => true,"compte" => $compte], 200);
    }

    public function update(int $id, Request $request) {
        $compte = Compte::findOrFail($id);

        $validated = $request->validate([
            'sexe' => 'nullable|string',
            'nom' => 'nullable|string',
            'prenom' => 'nullable|string',
            'email' => 'nullable|email|unique:compte,email,'.$id,
            'date_naissance' => 'nullable|date',
            'mdp' => 'nullable|string|min:8',
            // Ajoutez les autres champs selon vos besoins
            'nationalite' => 'nullable|string',
            'titre' => 'nullable|string',
            'adresse' => 'nullable|string',
            'adresse_comp' => 'nullable|string',
            'cp' => 'nullable|string',
            'ville' => 'nullable|string',
            'pays' => 'nullable|string',
            'numero' => 'nullable|string',
            'website' => 'nullable|string',
        ]);

        if (isset($validated['mdp'])) {
            $validated['mdp_crypted'] = (new Compte())->hashPwd($validated['mdp']);
            //unset($validated['mdp']);
        }

        $compte->update($validated);

        return response()->json(['message' => 'Compte updated successfully', 'compte' => $compte, 'success' => true], 200);
    }

    public function create(Request $request) {
        $validated = $request->validate([
            'sexe' => 'nullable|string',
            'nom' => 'required|string',
            'prenom' => 'required|string',
            'email' => 'required|email|unique:compte,email',
            'date_naissance' => 'nullable|date',
            'mdp' => 'required|string'/*+'|min:8'*/,  // Assurez-vous que 'mdp' est fourni
            // Ajoutez les autres champs selon vos besoins
            'nationalite' => 'nullable|string',
            'titre' => 'nullable|string',
            'adresse' => 'nullable|string',
            'adresse_comp' => 'nullable|string',
            'cp' => 'nullable|string',
            'ville' => 'nullable|string',
            'pays' => 'nullable|string',
            'numero' => 'nullable|string',
            'website' => 'nullable|string',
        ]);

        $validated['mdp_crypted'] = (new Compte())->hashPwd($validated['mdp']);
        // unset($validated['mdp']);  // Supprimez le mot de passe en clair

        return response()->json(['message' => 'Compte created successfully', 'compte' => Compte::create($validated), 'success' => true], 201);
    }
}
