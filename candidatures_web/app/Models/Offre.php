<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Offre extends Model
{
    use HasFactory;
    protected $table = 'offre';

    protected $fillable = ['titre','type','description','nom_entreprise','adresse_entreprise','adresse_comp_entreprise','cp_entreprise','ville_entreprise','pays_entreprise','nom_recruteur','prenom_recruteur','email_entreprise','tel_entreprise','periode','salaire_min','salaire_max','date_publication'];

    public function candidatures()
    {
        return $this->hasMany(Candidature::class, 'offre', 'id');
    }
}
?>