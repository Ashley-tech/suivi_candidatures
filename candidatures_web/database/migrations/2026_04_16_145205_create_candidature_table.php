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
        Schema::create('candidature', function (Blueprint $table) {
            $table->id();

            $table->foreignId('compte')->constrained('compte','id');
            $table->foreignId('offre')->constrained('offre','id');
            $table->foreignId('cv')->constrained('cv','id');

            $table->string('statut');
            $table->date('date_candidature');
            $table->decimal('score_matching', 5, 2)->nullable();

            $table->unique(['compte', 'offre']); // 🔥 IMPORTANT

            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('candidature');
    }
};
