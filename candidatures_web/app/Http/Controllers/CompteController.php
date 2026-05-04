<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Compte;

class CompteController extends Controller
{
    public function getAll() {
        return Compte::all();
    }

    public function login(Request $request){
        $request->validate([
            'email' => 'required|email',
            'mdp' => 'required|string',
        ]);

        $compte = Compte::where('email', $request->email)->first();
        if ($compte && password_verify($request->mdp, $compte->mdp_crypted)) {
            return response()->json(['message' => 'Login successful','success' => true,'code' => 200, 'compte' => $compte]);
        } else {
            return response()->json(['message' => 'Invalid credentials','success' => false, 'code' => 401]);
        }
    }

    public function delete(int $id) {
        $compte = Compte::findOrFail($id);
        $compte->delete();

        return response()->json(['message' => 'Compte deleted successfully','success' => true,'code' => 200]);
    }

    public function updatePwd(int $id, Request $request) {
        $compte = Compte::findOrFail($id);

        $validated = $request->validate([
            'mdp' => 'required|string',
        ]);

        $compte->mdp_crypted = (new Compte())->hashPwd($validated['mdp']);
        $compte->mdp = $validated['mdp']; // Stockez le mot de passe en clair pour la réponse (optionnel, à ne pas faire en production)
        $compte->save();

        return response()->json(['message' => 'Password updated successfully','success' => true,'code' => 200, "compte" => $compte]);
    }

    public function update(int $id, Request $request) {
        $compte = Compte::findOrFail($id);

        $validated = $request->validate([
            'sexe' => 'nullable|string',
            'nom' => 'nullable|string',
            'prenom' => 'nullable|string',
            'email' => 'nullable|email',
            'date_naissance' => 'nullable|date',
            'mdp' => 'nullable|string'/*+'|min:8'*/,  // Assurez-vous que 'mdp' est fourni si vous souhaitez le mettre à jour
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
        if ($validated['email'] != $compte->email) {
            $validated['email'] = $validated['email'] ?? $compte->email;
        } else {
            $validated['email'] = $compte->email;
        }
        $comptes = CompteController::getAll();
        for ($i = 0; $i < count($comptes); $i++) {
            if ($comptes[$i]->email == $validated['email'] && $comptes[$i]->id != $compte->id) {
                return response()->json(['message' => 'Email already in use by another account','success' => false,'code' => 400]);
            }
        }

        /*$validated['sexe'] = $validated['sexe'] ?? $compte->sexe;
        $validated['nom'] = $validated['nom'] ?? $compte->nom;
        $validated['prenom'] = $validated['prenom'] ?? $compte->prenom;
        $validated['email'] = $validated['email'] ?? $compte->email;
        $validated['date_naissance'] = $validated['date_naissance'] ?? $compte->date_naissance;
        $validated['nationalite'] = $validated['nationalite'] ?? $compte->nationalite;
        $validated['titre'] = $validated['titre'] ?? $compte->titre;
        $validated['adresse'] = $validated['adresse'] ?? $compte->adresse;
        $validated['adresse_comp'] = $validated['adresse_comp'] ?? $compte->adresse_comp;
        $validated['cp'] = $validated['cp'] ?? $compte->cp;
        $validated['ville'] = $validated['ville'] ?? $compte->ville;
        $validated['pays'] = $validated['pays'] ?? $compte->pays;
        $validated['numero'] = $validated['numero'] ?? $compte->numero;
        $validated['website'] = $validated['website'] ?? $compte->website;*/

        $compte->update($validated);

        return response()->json(['message' => 'Compte updated successfully', 'compte' => $compte, 'success' => true,'code' => 200]);
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

        return response()->json(['message' => 'Compte created successfully', 'compte' => Compte::create($validated), 'success' => true,'code' => 201]);
    }
}
