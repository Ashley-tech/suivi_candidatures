<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

use App\Http\Controllers\CVController;
use App\Models\CV;

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

Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
    return $request->user();
});

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
