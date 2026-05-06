<?php

namespace Database\Factories;

use Illuminate\Database\Eloquent\Factories\Factory;
use App\Models\Compte;

class CVFactory extends Factory
{
    public function definition(): array
    {
        return [
            'nom' => 'CV ' . $this->faker->lastName(),
            'contenu' => $this->faker->text(500),
            'date_upload' => now(),
            'visible' => $this->faker->boolean(),
            'compte' => Compte::factory(), // 🔥 relation auto
        ];
    }
}