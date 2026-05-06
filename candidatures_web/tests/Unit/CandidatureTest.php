<?php

namespace Tests\Unit;

use Tests\TestCase;
use App\Models\Compte;
use App\Models\Offre;
use App\Models\CV;
use App\Models\Candidature;
use Illuminate\Foundation\Testing\RefreshDatabase;

class CandidatureTest extends TestCase
{
    use RefreshDatabase;

    public function test_creation_candidature()
    {
        $compte = Compte::factory()->create();
        $offre = Offre::factory()->create();
        $cv = CV::factory()->create(['compte' => $compte->id]);

        $candidature = Candidature::create([
            'compte' => $compte->id,
            'offre' => $offre->id,
            'cv' => $cv->id,
            'statut' => 'envoyé',
            'date_candidature' => now()
        ]);

        $this->assertDatabaseHas('candidature', [
            'compte' => $compte->id,
            'offre' => $offre->id
        ]);
    }
}