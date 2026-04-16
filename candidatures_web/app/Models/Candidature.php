<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Candidature extends Model
{
    use HasFactory;

    protected $table = 'candidature';
    public $incrementing = false;
    public $timestamps = false;

    protected $fillable = [
        'compte',
        'offre',
        'cv',
        'statut',
        'date_candidature',
        'score_matching',
    ];

    public function compte()
    {
        return $this->belongsTo(Compte::class, 'compte');
    }

    public function offre()
    {
        return $this->belongsTo(Offre::class, 'offre');
    }

    public function cv()
    {
        return $this->belongsTo(CV::class, 'cv','id');
    }
}
