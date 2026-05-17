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
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

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
    lateinit var statut : EditText
    lateinit var email_tel_recruteur : EditText
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
    var ta = ""
    var tc = ""
    val cvIds = ArrayList<Int>()
    val cvNames = ArrayList<String>()
    var idCompte = 0
    var e = ""
    var t = ""
    var st = ""
    var idCV = 0
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
        email_tel_recruteur = findViewById(R.id.email_tel_recruteur_input)
        statut = findViewById(R.id.statut_input)
        periode = findViewById(R.id.periode_input)
        salaire_max = findViewById(R.id.salaire_max_input)
        salaire_min = findViewById(R.id.salaire_min_input)
        dpublication = findViewById(R.id.date_publication_input)
        cv = findViewById(R.id.cvs_spinner)

        var a = AccountManagement(this)
        AsyncLoadCVs().execute(a.getLogin().toString())
    }

    fun regexCheck(re: Regex, str: String):Boolean{
        return re.matches(str)
    }

    override fun onClick(v: View){
        when (v.getId()){
            R.id.add_candidature_button -> {
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
                AsyncAddOffre().execute(tc,titre.text.toString(),details.text.toString(),entreprise.text.toString(),adresse,comp,cp,ville,pays,nr,pr,e,t,periode.text.toString(),salaire_min.text.toString(),salaire_max.text.toString(),st,dpublication.text.toString())
            }
            R.id.new_to_candidature_button -> {
                finish()
            }
            else -> {

            }
        }
    }

    private inner class AsyncAddOffre : AsyncTask<String, Void, String>() {
        private val pdLoading = ProgressDialog(this@NewCandidatureActivity)
        private var co: HttpURLConnection? = null
        private var url: URL? = null
        private var offre_id: Int = 0
        override fun onPreExecute() {
            super.onPreExecute()

            pdLoading.setMessage("Ajout de l'offre...")
            pdLoading.setCancelable(false)
            pdLoading.show()
        }

        override fun doInBackground(vararg strings: String?): String {
            try {
                url = URL("http://10.0.2.2:8000/api/offres")
            } catch (e: MalformedURLException) {
                e.printStackTrace()
                return "false"
            }

            try {
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
                    offre_id = json.getInt("offre_id")
                    AsyncAddCandidature().execute(offre_id.toString(),idCompte.toString(),dcandidature.text.toString(),st,idCV.toString())
                } else {
                    Toast.makeText(
                        this@NewCandidatureActivity,
                        "Echec d'ajout de l'offre",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@NewCandidatureActivity,
                    "Erreur de parsing JSON",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private inner class AsyncAddCandidature : AsyncTask<String, Void, String>() {
        private val pdLoading = ProgressDialog(this@NewCandidatureActivity)
        private var co: HttpURLConnection? = null
        private var url: URL? = null
        override fun onPreExecute() {
            super.onPreExecute()

            pdLoading.setMessage("Ajout de la candidature...")
            pdLoading.setCancelable(false)
            pdLoading.show()
        }

        override fun doInBackground(vararg strings: String?): String {
            try {
                url = URL("http://10.0.2.2:8000/api/candidatures")
            } catch (e: MalformedURLException) {
                e.printStackTrace()
                return "false"
            }

            try {
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
                    .appendQueryParameter("offre", strings[0])
                    .appendQueryParameter("compte", strings[1])
                    .appendQueryParameter("date_candidature", strings[2])
                    .appendQueryParameter("statut", strings[3])
                    .appendQueryParameter("cv", strings[4])

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
                        this@NewCandidatureActivity,
                        "Candidature ajoutée avec succès ! Redirection vers la page précédente...",
                        Toast.LENGTH_LONG
                    ).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(
                        this@NewCandidatureActivity,
                        "Echec d'ajout de la candidature",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@NewCandidatureActivity,
                    "Erreur de parsing JSON",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private inner class AsyncLoadCVs : AsyncTask<String, Void, String>() {

        private val pdLoading = ProgressDialog(this@NewCandidatureActivity)

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
                    this@NewCandidatureActivity,
                    android.R.layout.simple_spinner_item,
                    cvNames
                )

                adapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
                )

                cv.adapter = adapter

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@NewCandidatureActivity,
                    "Erreur parsing JSON",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}