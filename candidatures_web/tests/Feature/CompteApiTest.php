<?php

namespace Tests\Feature;

use Tests\TestCase;
use App\Models\Compte;
use Illuminate\Foundation\Testing\RefreshDatabase;

class CompteApiTest extends TestCase
{
    use RefreshDatabase;

    public function test_get_comptes()
    {
        Compte::factory()->count(3)->create();

        $response = $this->get('/api/comptes');

        $response->assertStatus(200);
        $response->assertJsonCount(3);
    }

    public function test_create_compte()
    {
        $response = $this->postJson('/api/comptes', [
            'nom' => 'Test',
            'prenom' => 'User',
            'email' => 'user@test.com',
            'sexe' => 'M',
            'mdp' =>  'tatatititoto'
        ]);
        $response->dump();

        $response->assertStatus(200);

        $this->assertDatabaseHas('compte', [
            'email' => 'user@test.com'
        ]);
    }
}
