<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

use App\Http\Controllers\CVController;
use App\Models\CV;
use App\Http\Controllers\CandidatureController;
use App\Http\Controllers\MailController;
use App\Models\Compte;
use App\Models\Candidature;
use App\Models\Offre;
use App\Http\Controllers\CompteController;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "api" middleware group. Make something great!
|
*/

/*Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
    return $request->user();
});*/

Route::post('/cv/upload', [CVController::class, 'enregistrerFile']);

Route::get('/cv/{id}/download', function ($id) {
    $cv = CV::findOrFail($id);

    return response($cv->contenu)
        ->header('Content-Type', $cv->mime_type)
        ->header('Content-Disposition', 'attachment; filename="'.$cv->nom.'"');
});

Route::get("/{compte}/cvs", function ($compte) {
    return CV::where('compte', $compte)->get()->map(function ($cv) {
        return [
            'id' => $cv->id,
            'nom' => $cv->nom,
            'download_url' => url("/api/cvs/{$cv->id}/download")
        ];
    });
});

Route::patch("/candidature/{id}/statut", [CandidatureController::class, 'updateStatut']);

Route::get('/comptes', function () {
    return Compte::all();
});

Route::post('/compte/{id}/update-pwd', [CompteController::class, 'updatePwd']);

Route::post('/comptes', [CompteController::class, 'create']);

Route::post('/login', [CompteController::class, 'login']);

Route::get("/comptes/{id}", function ($id) {
    return Compte::find($id);
});

Route::get("/candidatures", function () {
    return Candidature::all();
});

Route::get("/compte/{compte}/candidatures", function ($compte) {
    return Candidature::where('compte', $compte)->get();
});

Route::get("/offres", function () {
    return Offre::all();
});

Route::get("/offres/{id}", function ($id) {
    return Offre::find($id);
});

Route::get('/test-mail/{address}', [MailController::class, 'envoyerMail']);

Route::post('/test-mail', [MailController::class, 'envoyerMail']);