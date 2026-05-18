package com.example.suivicandidatures

import android.app.ProgressDialog
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale

class ModifyCandidatureActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var extras : Bundle
    lateinit var modifier : Button
    lateinit var retour : Button
    lateinit var type_a : Spinner
    lateinit var titre : EditText
    lateinit var details : EditText
    lateinit var entreprise : EditText
    lateinit var adresse_entreprise : EditText
    lateinit var cvp : EditText
    lateinit var dcandidature : EditText
    lateinit var recruteur : EditText
    lateinit var statut : EditText
    lateinit var email_tel_recruteur : EditText
    lateinit var periode : EditText
    lateinit var salaire_min : EditText
    lateinit var salaire_max : EditText
    lateinit var dpublication : EditText
    lateinit var cv : Spinner
    lateinit var a : AccountManagement
    var c= 0
    var offre = 0
    var adresse = ""
    var comp = ""
    var cp = ""
    var ville = ""
    var pays  = ""
    var nr = ""
    var pr = ""
    var ta = ""
    var tc = ""
    val cvIds = ArrayList<Int>()
    val cvNames = ArrayList<String>()
    var idCompte = 0
    var e = ""
    var t = ""
    var st = ""
    var idCV = 0
    var id_cv = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_candidature)
        type_a = findViewById(R.id.type_candidature_spinner_2)
        titre = findViewById(R.id.titre_candidature_input_2)
        details = findViewById(R.id.details_input_2)
        entreprise = findViewById(R.id.nom_entreprise_input_2)
        adresse_entreprise = findViewById(R.id.adresse_entreprise_input_2)
        cvp = findViewById(R.id.cp_ville_pays_entreprise_input_2)
        dcandidature = findViewById(R.id.date_candidature_input_2)
        recruteur = findViewById(R.id.nom_prenom_recruteur_input_2)
        email_tel_recruteur = findViewById(R.id.email_tel_recruteur_input_2)
        statut = findViewById(R.id.statut_input_2)
        periode = findViewById(R.id.periode_input_2)
        salaire_max = findViewById(R.id.salaire_max_input_2)
        salaire_min = findViewById(R.id.salaire_min_input_2)
        dpublication = findViewById(R.id.date_publication_input_2)
        cv = findViewById(R.id.cvs_spinner_2)
        modifier = findViewById(R.id.modify_candidature_button)
        modifier.setOnClickListener(this)
        retour = findViewById(R.id.modify_to_candidature_info_button)
        retour.setOnClickListener(this)
        extras = intent.extras!!
        c = extras.getInt("id_candidature")
        offre = extras.getInt("id_offre")
        AsyncLoadOffer().execute()
        a = AccountManagement(this)

    }

    override fun onClick(v: View) {
        when (v.getId()){
            R.id.modify_to_candidature_info_button -> {
                finish()
            }
            R.id.modify_candidature_button -> {
                val position = cv.selectedItemPosition
                idCV = cvIds[position]
                if (titre.text.toString() == "" || idCV == 0){
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
                if (statut.text.toString() == ""){
                    st = "En attente"
                } else {
                    st = statut.text.toString()
                }
                part_champ = recruteur.text.toString().split("/")
                if (recruteur.text.toString() != "" && part_champ.size > 2){
                    Toast.makeText(this,"Merci de respecter le format [Nom]/[Prénom du recruteur] car il y a trop d'arguments : "+part_champ.size+" > 2",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (part_champ.size != 0){
                    nr = part_champ[0]
                    if (part_champ.size == 2){
                        pr = part_champ[1]
                    } else {
                        pr = ""
                    }
                } else {
                    nr=""
                    pr=""
                }
                part_champ = email_tel_recruteur.text.toString().split("/")
                if (email_tel_recruteur.text.toString() != "" && part_champ.size > 2){
                    Toast.makeText(this,"Merci de respecter le format [E-mail]/[Téléphone du recruteur] car il y a trop d'arguments : "+part_champ.size+" > 2",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (part_champ.size != 0){
                    e = part_champ[0]
                    if (part_champ.size == 2){
                        t = part_champ[1]
                    } else {
                        t = ""
                    }
                } else {
                    e = ""
                    t = ""
                }
                val regexEmail = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
                if (e != "" && !regexCheck(regexEmail,e)){
                    Toast.makeText(this,"L'adresse mail du recruteur n'est pas correct.",Toast.LENGTH_SHORT).show();
                    return;
                }
                var regexTel = Regex("^\\d{10}$")
                if (t != "" && !regexCheck(regexTel,t)){
                    Toast.makeText(this,"Le numéro de téléphone doit être composé de 10 chiffres exactement",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (salaire_min.text.toString() != "") {
                    Log.i("min", salaire_min.text.toString().toFloat().toString())
                }
                if (salaire_min.text.toString() != "" && salaire_min.text.toString().toFloat() < 0){
                    Toast.makeText(this,"Le salaire minimum doit être supérieur à 0.",Toast.LENGTH_SHORT).show();
                    return
                }
                if (salaire_max.text.toString() != "") {
                    Log.i("max",salaire_max.text.toString().toFloat().toString())
                }
                if (salaire_max.text.toString() != "" && salaire_max.text.toString().toFloat() <= 0){
                    Toast.makeText(this,"Le salaire maximum doit être supérieur ou égal à 0.",Toast.LENGTH_SHORT).show();
                    return
                }
                if (salaire_min.text.toString() != "" && salaire_max.text.toString() != "" && salaire_max.text.toString().toFloat() < salaire_min.text.toString().toFloat()){
                    Toast.makeText(this,"Le salaire maximum doit être supérieur ou égal au salaire minimum.",Toast.LENGTH_SHORT).show();
                    return
                }
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE)
                sdf.isLenient = false
                if (dcandidature.text.toString()!= "" && regexCheck(regexD,dcandidature.text.toString())) {
                    Log.i("candidature", sdf.parse(dcandidature.text.toString()).toString())
                }
                if (dpublication.text.toString()!= "" && regexCheck(regexD,dpublication.text.toString())) {
                    Log.i("publication",sdf.parse(dpublication.text.toString()).toString())
                }
                if (dpublication.text.toString() != "" && dcandidature.text.toString() != "" && sdf.parse(dpublication.text.toString()) > sdf.parse(dcandidature.text.toString())){
                    Toast.makeText(this,"La date de publication de l'offre doit être antérieure ou égale à la date de candidature.",Toast.LENGTH_SHORT).show();
                    return
                }
                Log.d("CV_SELECTIONNE", idCV.toString())
                Log.d("type",type_a.getSelectedItem().toString())
                if (!type_a.getSelectedItem().toString().contains("Sélectionnez un type d'offre")){
                    tc = type_a.getSelectedItem().toString()
                } else {
                    tc = ""
                }
                AsyncModifyOffre().execute(tc,titre.text.toString(),details.text.toString(),entreprise.text.toString(),adresse,comp,cp,ville,pays,nr,pr,e,t,periode.text.toString(),salaire_min.text.toString(),salaire_max.text.toString(),st,dpublication.text.toString())
            }
            else -> {

            }
        }
    }

    fun regexCheck(re: Regex, str: String):Boolean{
        return re.matches(str)
    }

    private inner class AsyncLoadOffer : AsyncTask<Void, Void, String>() {
        private val pdLoading = ProgressDialog(this@ModifyCandidatureActivity)
        private var co: HttpURLConnection? = null
        private var url: URL? = null

        override fun onPreExecute() {
            super.onPreExecute()

            pdLoading.setMessage("Chargement de l'offre...")
            pdLoading.setCancelable(false)
            pdLoading.show()
        }

        override fun doInBackground(vararg params: Void?): String {
            try {
                url = URL("http://10.0.2.2:8000/api/offres/$offre")

                co = url!!.openConnection() as HttpURLConnection

                co!!.readTimeout = 15000
                co!!.connectTimeout = 15000
                co!!.requestMethod = "GET"

                co!!.setRequestProperty(
                    "Content-Type",
                    "application/json"
                )

                co!!.doInput = true

                co!!.connect()

                val responseCode2 = co!!.responseCode

                if (responseCode2 == HttpURLConnection.HTTP_OK) {

                    val input2: InputStream = co!!.inputStream
                    val reader2 = BufferedReader(InputStreamReader(input2))

                    val result2 = StringBuilder()

                    var line2: String?

                    while (reader2.readLine().also { line2 = it } != null) {
                        result2.append(line2)
                    }

                    return result2.toString()

                } else {
                    return "Unsuccessful"
                }

            } catch (e: Exception) {
                e.printStackTrace()
                return "Exception"
            } finally {
                co?.disconnect()
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            pdLoading.dismiss()

            if (result == null) return
            AsyncLoadCandidature().execute()

            try {
                var j = JSONObject(result)
                when (j.getString("type")) {
                    "Alternance" -> type_a.setSelection(1)
                    "Stage" -> type_a.setSelection(2)
                    "CDI" -> type_a.setSelection(3)
                    "CDD" -> type_a.setSelection(4)
                    "Freelance" -> type_a.setSelection(5)
                    "Autre" -> type_a.setSelection(6)
                    else -> type_a.setSelection(0)
                }
                titre.setText((j.optString("titre").takeIf { it != "null" } ?: ""))
                details.setText((j.optString("description").takeIf { it != "null" } ?: ""))
                entreprise.setText((j.optString("nom_entreprise").takeIf { it != "null" } ?: ""))
                if ((j.optString("adresse_comp_entreprise").takeIf { it != "null" } ?: "") != "") {
                    adresse_entreprise.setText((j.optString("adresse_entreprise").takeIf { it != "null" }
                        ?: "") + "/" + (j.optString("adresse_comp_entreprise").takeIf { it != "null" }
                        ?: ""))
                } else {
                    adresse_entreprise.setText((j.optString("adresse_entreprise").takeIf { it != "null" }
                        ?: ""))
                }
                if ((j.optString("pays_entreprise").takeIf { it != "null" } ?: "") != ""){
                    cvp.setText((j.optString("cp_entreprise").takeIf { it != "null" } ?: "")+ "/"+(j.optString("ville_entreprise").takeIf { it != "null" } ?: "")+"/"+(j.optString("pays_entreprise").takeIf { it != "null" } ?: ""))
                } else if ((j.optString("ville_entreprise").takeIf { it != "null" } ?: "") != ""){
                    cvp.setText((j.optString("cp_entreprise").takeIf { it != "null" } ?: "")+ "/"+(j.optString("ville_entreprise").takeIf { it != "null" } ?: ""))
                } else {
                    cvp.setText((j.optString("cp_entreprise").takeIf { it != "null" } ?: ""))
                }
                statut.setText((j.optString("statut").takeIf { it != "null" } ?: ""))
                if ((j.optString("prenom_recruteur").takeIf { it != "null" } ?: "") != "") {
                    recruteur.setText((j.optString("nom_recruteur").takeIf { it != "null" }
                        ?: "") + "/" + (j.optString("prenom_recruteur").takeIf { it != "null" }
                        ?: ""))
                } else {
                    recruteur.setText((j.optString("nom_recruteur").takeIf { it != "null" }
                        ?: ""))
                }
                if ((j.optString("tel_entreprise").takeIf { it != "null" } ?: "") != "") {
                    email_tel_recruteur.setText((j.optString("email_entreprise").takeIf { it != "null" }
                        ?: "") + "/" + (j.optString("tel_entreprise").takeIf { it != "null" }
                        ?: ""))
                } else {
                    email_tel_recruteur.setText((j.optString("email_entreprise").takeIf { it != "null" }
                        ?: ""))
                }
                periode.setText( (j.optString("periode").takeIf { it != "null" } ?: ""))
                salaire_max.setText((j.optString("salaire_max").takeIf { it != "null" } ?: ""))
                salaire_min.setText((j.optString("salaire_min").takeIf { it != "null" } ?: ""))
                dpublication.setText((j.optString("date_publication").takeIf { it != "null" } ?: ""))
                AsyncLoadCVs().execute(a.getLogin())
            } catch (e: Exception) {

                e.printStackTrace()

                Toast.makeText(
                    this@ModifyCandidatureActivity,
                    "Erreur de parsing JSON",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private inner class AsyncLoadCandidature : AsyncTask<Void, Void, String>() {
        private val pdLoading = ProgressDialog(this@ModifyCandidatureActivity)
        private var co: HttpURLConnection? = null
        private var url: URL? = null

        override fun onPreExecute() {
            super.onPreExecute()

            pdLoading.setMessage("Chargement de la candidature...")
            pdLoading.setCancelable(false)
            pdLoading.show()
        }

        override fun doInBackground(vararg params: Void?): String {
            try {

                /*
                 * Récupération des candidatures
                 */

                url = URL("http://10.0.2.2:8000/api/candidature/$c")

                co = url!!.openConnection() as HttpURLConnection

                co!!.readTimeout = 15000
                co!!.connectTimeout = 15000
                co!!.requestMethod = "GET"

                co!!.setRequestProperty(
                    "Content-Type",
                    "application/json"
                )

                co!!.doInput = true

                co!!.connect()

                val responseCode2 = co!!.responseCode

                if (responseCode2 == HttpURLConnection.HTTP_OK) {

                    val input2: InputStream = co!!.inputStream
                    val reader2 = BufferedReader(InputStreamReader(input2))

                    val result2 = StringBuilder()

                    var line2: String?

                    while (reader2.readLine().also { line2 = it } != null) {
                        result2.append(line2)
                    }

                    return result2.toString()

                } else {
                    return "Unsuccessful"
                }

            } catch (e: Exception) {
                e.printStackTrace()
                return "Exception"
            } finally {
                co?.disconnect()
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            pdLoading.dismiss()

            if (result == null) return

            try {
                var j = JSONObject(result)
                statut.setText((j.optString("statut").takeIf { it != "null" } ?: ""))
                dcandidature.setText((j.optString("date_candidature").takeIf { it != "null" } ?: ""))
                id_cv = j.getInt("cv")
            } catch (e: Exception) {
                e.printStackTrace()

                Toast.makeText(
                    this@ModifyCandidatureActivity,
                    "Erreur de parsing JSON",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private inner class AsyncLoadCVs : AsyncTask<String, Void, String>() {

        private val pdLoading = ProgressDialog(this@ModifyCandidatureActivity)

        private var co: HttpURLConnection? = null
        private var url: URL? = null

        override fun onPreExecute() {
            super.onPreExecute()

            pdLoading.setMessage("Chargement des CVs...")
            pdLoading.setCancelable(false)
            pdLoading.show()
        }

        override fun doInBackground(vararg params: String?): String {

            /*
             * Etape 1 :
             * Récupérer le compte avec email
             */

            try {
                url = URL("http://10.0.2.2:8000/api/compte/find-by-email")

                co = url!!.openConnection() as HttpURLConnection
                co!!.readTimeout = 15000
                co!!.connectTimeout = 15000
                co!!.requestMethod = "POST"

                co!!.setRequestProperty(
                    "Content-Type",
                    "application/x-www-form-urlencoded"
                )

                co!!.doInput = true
                co!!.doOutput = true

                val builder = Uri.Builder()
                    .appendQueryParameter("email", params[0])

                val query = builder.build().encodedQuery

                val os: OutputStream = co!!.outputStream

                val writer = BufferedWriter(
                    OutputStreamWriter(os, "UTF-8")
                )

                writer.write(query)
                writer.flush()
                writer.close()

                os.close()

                co!!.connect()

            } catch (e: Exception) {
                e.printStackTrace()
                return "Exception"
            }

            try {
                val responseCode = co!!.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val input: InputStream = co!!.inputStream
                    val reader = BufferedReader(
                        InputStreamReader(input)
                    )

                    val result = StringBuilder()

                    var line: String?

                    while (reader.readLine().also { line = it } != null) {
                        result.append(line)
                    }

                    /*
                     * JSON compte
                     */

                    val json = JSONObject(result.toString())

                    val compte = json.getJSONObject("compte")

                    idCompte = compte.getInt("id")

                }

            } catch (e: Exception) {
                e.printStackTrace()
                return "Exception"
            } finally {
                co?.disconnect()
            }

            /*
             * Etape 2 :
             * Charger les CVs
             */

            return try {

                url = URL("http://10.0.2.2:8000/api/compte/$idCompte/cvs")

                co = url!!.openConnection() as HttpURLConnection

                co!!.readTimeout = 15000
                co!!.connectTimeout = 15000

                co!!.requestMethod = "GET"

                co!!.setRequestProperty(
                    "Content-Type",
                    "application/json"
                )

                co!!.doInput = true

                co!!.connect()

                val responseCode = co!!.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {

                    val input: InputStream = co!!.inputStream

                    val reader = BufferedReader(
                        InputStreamReader(input)
                    )

                    val result = StringBuilder()

                    var line: String?

                    while (reader.readLine().also { line = it } != null) {
                        result.append(line)
                    }

                    result.toString()

                } else {
                    "Unsuccessful"
                }

            } catch (e: Exception) {
                e.printStackTrace()
                "Exception"
            } finally {
                co?.disconnect()
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            pdLoading.dismiss()

            if (result == null) return

            try {
                val jsonArray = JSONArray(result)

                cvIds.clear()
                cvNames.clear()
                cvIds.add(0)
                cvNames.add("--Sélectionnez un CV--")

                for (i in 0 until jsonArray.length()) {
                    val cv = jsonArray.getJSONObject(i)
                    val visible = cv.getInt("visible")
                    if (visible == 1) {
                        val id = cv.getInt("id")
                        val nom = cv.getString("nom")

                        cvIds.add(id)
                        cvNames.add(nom)
                    }
                }

                /*
                 * Adapter Spinner
                 */

                val adapter = ArrayAdapter(
                    this@ModifyCandidatureActivity,
                    android.R.layout.simple_spinner_item,
                    cvNames
                )

                adapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
                )

                cv.adapter = adapter

                // Sélection du bon CV
                val index = cvIds.indexOf(id_cv)

                if (index != -1) {
                    cv.setSelection(index)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@ModifyCandidatureActivity,
                    "Erreur parsing JSON",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private inner class AsyncModifyCandidature : AsyncTask<String, Void, String>() {
        private val pdLoading = ProgressDialog(this@ModifyCandidatureActivity)
        private var co: HttpURLConnection? = null
        private var url: URL? = null
        override fun onPreExecute() {
            super.onPreExecute()

            pdLoading.setMessage("Modification de la candidature...")
            pdLoading.setCancelable(false)
            pdLoading.show()
        }

        override fun doInBackground(vararg strings: String?): String {
            try {
                url = URL("http://10.0.2.2:8000/api/candidature/"+c)
            } catch (e: MalformedURLException) {
                e.printStackTrace()
                return "false"
            }

            try {
                co = url!!.openConnection() as HttpURLConnection
                co!!.readTimeout = 15000
                co!!.connectTimeout = 15000
                co!!.requestMethod = "PATCH"
                co!!.setRequestProperty(
                    "Content-Type",
                    "application/x-www-form-urlencoded"
                )

                co!!.doInput = true
                co!!.doOutput = true

                val builder = Uri.Builder()
                    .appendQueryParameter("date_candidature", strings[0])
                    .appendQueryParameter("statut", strings[1])
                    .appendQueryParameter("cv", strings[2])

                val query = builder.build().encodedQuery

                val os: OutputStream = co!!.outputStream

                val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))

                writer.write(query)
                writer.flush()
                writer.close()

                os.close()

                co!!.connect()

            } catch (e: IOException) {
                e.printStackTrace()
                return "Exception"
            }
            try {
                val responseCode = co!!.responseCode
                Log.d("HTTP_CODE", responseCode.toString())
                return if (responseCode == HttpURLConnection.HTTP_OK) {
                    val input: InputStream = co!!.inputStream
                    val reader = BufferedReader(InputStreamReader(input))
                    val result = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        result.append(line)
                    }
                    result.toString()
                } else {
                    "Unsuccessful"
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return "Exception"
            } finally {
                co?.disconnect()
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            pdLoading.dismiss()

            if (result == null) return

            try {
                val json = JSONObject(result)
                val success = json.getBoolean("success")
                if (success) {
                    Toast.makeText(
                        this@ModifyCandidatureActivity,
                        "Candidature modifiée avec succès ! Redirection vers la page précédente...",
                        Toast.LENGTH_LONG
                    ).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(
                        this@ModifyCandidatureActivity,
                        "Echec de modification de la candidature",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ModifyCandidatureActivity,
                    "Erreur de parsing JSON",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private inner class AsyncModifyOffre : AsyncTask<String, Void, String>() {
        private val pdLoading = ProgressDialog(this@ModifyCandidatureActivity)
        private var co: HttpURLConnection? = null
        private var url: URL? = null
        private var offre_id: Int = 0
        override fun onPreExecute() {
            super.onPreExecute()

            pdLoading.setMessage("Modification de l'offre...")
            pdLoading.setCancelable(false)
            pdLoading.show()
        }

        override fun doInBackground(vararg strings: String?): String {
            try {
                url = URL("http://10.0.2.2:8000/api/offres/"+offre)
            } catch (e: MalformedURLException) {
                e.printStackTrace()
                return "false"
            }

            try {
                co = url!!.openConnection() as HttpURLConnection
                co!!.readTimeout = 15000
                co!!.connectTimeout = 15000
                co!!.requestMethod = "PATCH"
                co!!.setRequestProperty(
                    "Content-Type",
                    "application/x-www-form-urlencoded"
                )

                co!!.doInput = true
                co!!.doOutput = true

                val builder = Uri.Builder()
                    .appendQueryParameter("type", strings[0])
                    .appendQueryParameter("titre", strings[1])
                    .appendQueryParameter("description", strings[2])
                    .appendQueryParameter("nom_entreprise", strings[3])
                    .appendQueryParameter("adresse_entreprise", strings[4])
                    .appendQueryParameter("adresse_comp_entreprise", strings[5])
                    .appendQueryParameter("cp_entreprise", strings[6])
                    .appendQueryParameter("ville_entreprise", strings[7])
                    .appendQueryParameter("pays_entreprise", strings[8])
                    .appendQueryParameter("nom_recruteur", strings[9])
                    .appendQueryParameter("prenom_recruteur", strings[10])
                    .appendQueryParameter("email_entreprise", strings[11])
                    .appendQueryParameter("tel_entreprise", strings[12])
                    .appendQueryParameter("periode", strings[13])
                    .appendQueryParameter("salaire_min", strings[14])
                    .appendQueryParameter("salaire_max", strings[15])
                    .appendQueryParameter("statut", strings[16])
                    .appendQueryParameter("date_publication", strings[17])

                val query = builder.build().encodedQuery

                val os: OutputStream = co!!.outputStream

                val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))

                writer.write(query)
                writer.flush()
                writer.close()

                os.close()

                co!!.connect()

            } catch (e: IOException) {
                e.printStackTrace()
                return "Exception"
            }
            try {
                val responseCode = co!!.responseCode
                Log.d("HTTP_CODE", responseCode.toString())
                return if (responseCode == HttpURLConnection.HTTP_OK) {
                    val input: InputStream = co!!.inputStream
                    val reader = BufferedReader(InputStreamReader(input))
                    val result = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        result.append(line)
                    }
                    result.toString()
                } else {
                    "Unsuccessful"
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return "Exception"
            } finally {
                co?.disconnect()
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            pdLoading.dismiss()

            if (result == null) return

            try {
                val json = JSONObject(result)
                val success = json.getBoolean("success")
                if (success) {
                    AsyncModifyCandidature().execute(dcandidature.text.toString(),st,idCV.toString())
                } else {
                    Toast.makeText(
                        this@ModifyCandidatureActivity,
                        "Echec d'ajout de l'offre",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ModifyCandidatureActivity,
                    "Erreur de parsing JSON",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}