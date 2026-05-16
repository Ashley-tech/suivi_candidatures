package com.example.suivicandidatures

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast

class NewCandidatureActivity : AppCompatActivity() , View.OnClickListener{
    lateinit var add : Button
    lateinit var retour : Button
    lateinit var type_a : Spinner
    lateinit var titre : EditText
    lateinit var details : EditText
    lateinit var entreprise : EditText
    lateinit var adresse_entreprise : EditText
    lateinit var cvp : EditText
    lateinit var dcandidature : EditText
    lateinit var recruteur : EditText
    lateinit var email_recruteur : EditText
    lateinit var tel_recruteur : EditText
    lateinit var periode : EditText
    lateinit var salaire_min : EditText
    lateinit var salaire_max : EditText
    lateinit var dpublication : EditText
    lateinit var cv : Spinner
    var adresse = ""
    var comp = ""
    var cp = ""
    var ville = ""
    var pays  = ""
    var nr = ""
    var pr = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_candidature)
        add = findViewById(R.id.add_candidature_button)
        retour = findViewById(R.id.new_to_candidature_button)
        add.setOnClickListener(this)
        retour.setOnClickListener(this)
        type_a = findViewById(R.id.type_candidature_spinner)
        titre = findViewById(R.id.titre_candidature_input)
        details = findViewById(R.id.details_input)
        entreprise = findViewById(R.id.nom_entreprise_input)
        adresse_entreprise = findViewById(R.id.adresse_entreprise_input)
        cvp = findViewById(R.id.cp_ville_pays_entreprise_input)
        dcandidature = findViewById(R.id.date_candidature_input)
        recruteur = findViewById(R.id.nom_prenom_recruteur_input)
        email_recruteur = findViewById(R.id.email_recruteur_input)
        tel_recruteur = findViewById(R.id.tel_recruteur_input)
        periode = findViewById(R.id.periode_input)
        salaire_max = findViewById(R.id.salaire_max_input)
        salaire_min = findViewById(R.id.salaire_min_input)
        dpublication = findViewById(R.id.date_publication_input)
        cv = findViewById(R.id.cvs_spinner)
    }

    fun regexCheck(re: Regex, str: String):Boolean{
        return re.matches(str)
    }

    override fun onClick(v: View){
        when (v.getId()){
            R.id.add_candidature_button -> {
                if (titre.text.toString() == ""){
                    Toast.makeText(this, "Les champs du titre et du CV sont obligatoires.",Toast.LENGTH_SHORT).show()
                    return
                }
                Log.i("details", details.text.toString())
                var part_champ = adresse_entreprise.text.toString().split("/")
                if (adresse_entreprise.text.toString() != "" && part_champ.size > 2){
                    Toast.makeText(this,"Merci de respecter le format [Adresse]/[Complément] car il y a trop d'arguments : "+part_champ.size+" > 2",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (adresse_entreprise.text.toString() != ""){
                    adresse = part_champ[0]
                    if (part_champ.size == 2){
                        comp=part_champ[1]
                    }else{
                        comp=""
                    }
                } else{
                    adresse=""
                    comp=""
                }
                part_champ = cvp.text.toString().split("/")
                if (cvp.text.toString() != "" && part_champ.size > 3){
                    Toast.makeText(this,"Merci de respecter le format [Code postal]/[Ville]/[Pays] car il y a trop d'arguments : "+part_champ.size+" > 3",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cvp.text.toString() != ""){
                    cp = part_champ[0]
                    if (part_champ.size >= 2) {
                        ville = part_champ[1]
                        if (part_champ.size == 3){
                            pays = part_champ[2]
                        } else{
                            pays=""
                        }
                    } else {
                        ville = ""
                        pays=""
                    }
                    pays = part_champ[2]
                } else {
                    cp = ""
                    ville=""
                    pays = ""
                }
                var regexCp = Regex("^\\d{5}$")
                if (cp != "" && !regexCheck(regexCp,cp)){
                    Toast.makeText(this,"Le code postal doit être composé de 5 chiffres exactement",Toast.LENGTH_SHORT).show();
                    return
                }
                var regexD = Regex("^\\d{4}-\\d{2}-\\d{2}$")
                if ((dcandidature.text.toString() != "" && !regexCheck(regexD,dcandidature.text.toString())) || (dpublication.text.toString() != "" && !regexCheck(regexD,dpublication.text.toString()))){
                    Toast.makeText(this,"Un des champs de date ne respecte pas le format AAAA-MM-DD",Toast.LENGTH_SHORT).show();
                    return
                }
            }
            R.id.new_to_candidature_button -> {
                finish()
            }
            else -> {

            }
        }
    }
}