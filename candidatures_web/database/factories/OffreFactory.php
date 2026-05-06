<?php

namespace Database\Factories;

use Illuminate\Database\Eloquent\Factories\Factory;

class OffreFactory extends Factory
{
    public function definition(): array
    {
        return [
            'titre' => $this->faker->jobTitle(),
            'type' => $this->faker->randomElement(['CDI', 'CDD', 'Stage', 'Alternance']),
            'description' => $this->faker->text(500),
            'nom_entreprise' => $this->faker->company(),
            'adresse_entreprise' => $this->faker->streetAddress(),
            'adresse_comp_entreprise' => null,
            'cp_entreprise' => $this->faker->postcode(),
            'ville_entreprise' => $this->faker->city(),
            'pays_entreprise' => $this->faker->country(),
            'nom_recruteur' => $this->faker->lastName(),
            'prenom_recruteur' => $this->faker->firstName(),
            'email_entreprise' => $this->faker->companyEmail(),
            'tel_entreprise' => $this->faker->phoneNumber(),
            'periode' => '2026',
            'salaire_min' => $this->faker->numberBetween(20000, 30000),
            'salaire_max' => $this->faker->numberBetween(30000, 60000),
            'date_publication' => now(),
        ];
    }
}