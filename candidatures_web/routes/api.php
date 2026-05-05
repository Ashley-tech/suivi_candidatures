<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

use App\Http\Controllers\CVController;
use App\Models\CV;
use App\Http\Controllers\CandidatureController;
use App\Http\Controllers\MailController;
use App\Http\Controllers\OffreController;
use App\Http\Controllers\RedisController;
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

Route::get("/compte/{compte}/cvs", function ($compte) {
    return CV::where('compte', $compte)->get()->map(function ($cv) {
        return [
            'id' => $cv->id,
            'nom' => $cv->nom,
            'download_url' => url("http://{$_SERVER['HTTP_HOST']}/api/cv/{$cv->id}/download"),
            'mime_type' => $cv->mime_type,
            'date_upload' => $cv->date_upload,
            'visible' => $cv->visible,
        ];
    });
});

Route::patch("/candidature/{id}/statut", [CandidatureController::class, 'updateStatut']);

Route::get('/comptes', function () {
    return Compte::all();
});

Route::patch('/compte/{id}/update-pwd', [CompteController::class, 'updatePwd']);

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

Route::patch("/compte/{id}", [CompteController::class, 'update']);

Route::post("/offres", [OffreController::class, 'ajouterOffre']);

Route::patch("/offres/{id}", [OffreController::class, 'updateOffre']);

Route::delete("/offres/{id}", [OffreController::class, 'deleteOffre']);           

Route::post("/candidatures", [CandidatureController::class, 'enregistrerCandidature']);

Route::patch("/candidature/{id}/save-score", [CandidatureController::class, 'saveScore']);

Route::delete("/candidature/{id}", [CandidatureController::class, 'deleteCandidature']);

Route::get("/redis/keys", [RedisController::class, 'getAllKeys']);
Route::get("/redis", [RedisController::class, 'getAllKeyValues']);
Route::get("/redis/{key}", [RedisController::class, 'getValue']);
Route::delete("/redis/{key}", [RedisController::class, 'deleteKey']);
Route::patch("/redis/{key}", [RedisController::class, 'setKey']);
Route::post("/redis", [RedisController::class, 'addNewKey']);

Route::post("/compte/find-by-email", [CompteController::class, 'findByEmail']);