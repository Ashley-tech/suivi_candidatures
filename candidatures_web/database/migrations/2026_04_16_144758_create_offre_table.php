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
        Schema::create('offre', function (Blueprint $table) {
            $table->id();
            $table->string('titre');
            $table->string('type')->nullable();
            $table->text('description')->nullable();
            $table->string('nom_entreprise')->nullable();
            $table->string('adresse_entreprise')->nullable();
            $table->string('adresse_comp_entreprise')->nullable();
            $table->string('cp_entreprise')->nullable();
            $table->string('ville_entreprise')->nullable();
            $table->string('pays_entreprise')->nullable();
            $table->string('nom_recruteur')->nullable();
            $table->string('prenom_recruteur')->nullable();
            $table->string('email_entreprise')->nullable();
            $table->string('tel_entreprise')->nullable();
            $table->string('periode')->nullable();
            $table->decimal('salaire_min', 10, 2)->nullable();
            $table->decimal('salaire_max', 10, 2)->nullable();
            $table->date('date_publication')->nullable();
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('offre');
    }
};
