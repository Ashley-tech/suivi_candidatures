<?php

use Illuminate\Support\Facades\Route;
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

Route::get("/login", function () {
    return view('login');
});

Route::get("/dashboard", function () {
    return view('dashboard');
});

Route::get("/signup", function () {
    return view('signup');
});

Route::get("/offres", function () {
    return view('offres');
});

Route::get("/offres/{id}", function ($id) {
    return view('offre_details', ['offre_id' => $id]);
});

Route::get("/offres/form/new", function () {
    return view('form_candidature');
});

Route::get("/compte/modify", function () {
    return view('modify_compte');
});

Route::get("/forgot-password", function () {
    return view('forgot');
});

Route::get("/cvs", function () {
    return view('cvs');
});

Route::get("/{compte}/new_password", function ($compte) {
    return view('new_password', ['compte_id' => $compte]);
});

Route::get("/cv/new", function () {
    return view('form_cv');
});