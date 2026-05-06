<?php

namespace Database\Factories;

use Illuminate\Database\Eloquent\Factories\Factory;
use App\Models\Compte;
use App\Models\Offre;
use App\Models\CV;

class CandidatureFactory extends Factory
{
    public function definition(): array
    {
        $compte = Compte::factory()->create();

        return [
            'compte' => $compte->id,
            'offre' => Offre::factory(),
            'cv' => CV::factory()->create([
                'compte' => $compte->id // 🔥 cohérence garantie
            ])->id,
            'statut' => $this->faker->randomElement([
                'envoyé',
                'entretien',
                'refusé',
                'accepté',
                'contrat signé'
            ]),
            'date_candidature' => now(),
            'score_matching' => $this->faker->randomFloat(2, 0, 100),
        ];
    }
}