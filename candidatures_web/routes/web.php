<?php

use Illuminate\Support\Facades\Route;
use App\Models\Compte;
use App\Models\Candidature;
use App\Models\Offre;
use App\Models\CV;
use Illuminate\Http\Request;

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "web" middleware group. Make something great!
|
*/

Route::get('/', function () {
    return view('welcome');
});

Route::get('/comptes', function () {
    return Compte::all();
});

Route::post('/comptes', function (Request $request) {
    return Compte::create($request->all());
});

Route::post('/login', function (Request $request) {
    $compte = Compte::where('email', $request->email)->first();
    if ($compte && password_verify($request->mdp, $compte->mdp_crypted)) {
        return response()->json(['message' => 'Login successful', 'compte' => $compte], 200);
    } else {
        return response()->json(['message' => 'Invalid credentials'], 401);
    }
});

Route::get("/comptes/{id}", function ($id) {
    return Compte::find($id);
});

Route::get("/candidatures", function () {
    return Candidature::all();
});

Route::get("/{compte}/candidatures", function ($compte) {
    return Candidature::where('compte', $compte)->get();
});

Route::get("/{compte}/cvs", function ($compte) {
    return CV::where('compte', $compte)->get();
});

Route::get("/offres", function () {
    return Offre::all();
});

Route::get("/offres/{id}", function ($id) {
    return Offre::find($id);
});