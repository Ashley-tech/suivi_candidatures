<?php
namespace Database\Factories;

use Illuminate\Database\Eloquent\Factories\Factory;

class CompteFactory extends Factory
{
    public function definition(): array
    {
        return [
            'sexe' => $this->faker->randomElement(['M', 'F']),
            'nom' => $this->faker->lastName(),
            'prenom' => $this->faker->firstName(),
            'email' => $this->faker->unique()->safeEmail(),
            'date_naissance' => $this->faker->date(),
            'mdp' => null,
            'mdp_crypted' => bcrypt('password'),
            'nationalite' => $this->faker->country(),
            'titre' => $this->faker->jobTitle(),
            'adresse' => $this->faker->streetAddress(),
            'adresse_comp' => null,
            'cp' => $this->faker->postcode(),
            'ville' => $this->faker->city(),
            'pays' => $this->faker->country(),
            'numero' => $this->faker->phoneNumber(),
            'website' => $this->faker->url(),
        ];
    }
}