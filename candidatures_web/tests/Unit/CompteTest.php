<?php

namespace Tests\Unit;

use Tests\TestCase;
use App\Models\Compte;
use Illuminate\Foundation\Testing\RefreshDatabase;

class CompteTest extends TestCase
{
    use RefreshDatabase;

    public function test_creation_compte()
    {
        $compte = Compte::create([
            'nom' => 'Dupont',
            'prenom' => 'Jean',
            'email' => 'jean@test.com',
            'sexe' => 'M',
            'mdp_crypted' => bcrypt('password')
        ]);

        $this->assertDatabaseHas('compte', [
            'email' => 'jean@test.com'
        ]);
    }
}