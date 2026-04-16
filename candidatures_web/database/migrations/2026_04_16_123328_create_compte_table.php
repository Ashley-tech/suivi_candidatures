<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('compte', function (Blueprint $table) {
            $table->id();
            $table->string('sexe')->nullable();
            $table->string('nom');
            $table->string('prenom');
            $table->string('email')->unique();
            $table->date('date_naissance')->nullable();
            $table->string('mdp')->nullable();
            $table->string('mdp_crypted')->nullable();
            $table->string('nationalite')->nullable();
            $table->string('titre')->nullable();
            $table->string('adresse')->nullable();
            $table->string('adresse_comp')->nullable();
            $table->string('cp')->nullable();
            $table->string('ville')->nullable();
            $table->string('pays')->nullable();
            $table->string('numero')->nullable();
            $table->string('website')->nullable();
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('compte');
    }
};
