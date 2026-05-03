<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class CV extends Model
{
    use HasFactory;

    protected $fillable = ['contenu','nom','mime_type','texte_extrait','compte','date_upload','visible'];
    protected $table = 'cv';

    public function compte()
    {
        return $this->belongsTo(Compte::class, 'compte', 'id');
    }

}
?>