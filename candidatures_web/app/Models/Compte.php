<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Compte extends Model
{
    use HasFactory;

    protected $fillable = ['sexe','nom','prenom','email','date_naissance','mdp','mdp_crypted','nationalite','titre','adresse','adresse_comp','cp','ville','pays','numero','website'];

    public function candidatures()
    {
        return $this->hasMany(Candidature::class, 'id');
    }

    public function cvs()
    {
        return $this->hasMany(CV::class, 'id');
    }
}
?>