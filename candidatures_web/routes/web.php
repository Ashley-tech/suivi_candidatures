<?php

use Illuminate\Support\Facades\Route;
use App\Models\Compte;
use App\Models\Candidature;
use App\Models\Offre;
use App\Models\CV;
use Illuminate\Http\Request;
use App\Http\Controllers\CompteController;
use App\Http\Controllers\CandidatureController;
use App\Http\Controllers\OffreController;

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

Route::get("/login", function () {
    return view('login');
});

Route::get("/dashboard", function () {
    return view('dashboard');
});

Route::get('/comptes', function () {
    return Compte::all();
});

Route::post('/comptes', [CompteController::class, 'create']);

Route::post('/login', [CompteController::class, 'login']);

Route::get("/comptes/{id}", function ($id) {
    return Compte::find($id);
});

Route::get("/candidatures", function () {
    return Candidature::all();
});

Route::get("/{compte}/candidatures", function ($compte) {
    return Candidature::where('compte', $compte)->get();
});

Route::get("/offres", function () {
    return Offre::all();
});

Route::get("/offres/{id}", function ($id) {
    return Offre::find($id);
});