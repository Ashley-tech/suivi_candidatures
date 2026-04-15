<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class CV extends Model
{
    use HasFactory;

    protected $fillable = ['contenu','nom','date_upload','visible'];

    public function compte()
    {
        return $this->belongsTo(Compte::class, 'compte', 'id');
    }

}
?>