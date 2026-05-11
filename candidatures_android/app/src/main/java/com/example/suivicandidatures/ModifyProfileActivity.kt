package com.example.suivicandidatures

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
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

class ModifyProfileActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var retour: Button;
    lateinit var valider: Button;
    lateinit var display_mdp: Button;
    lateinit var display_reconfirm_mdp: Button;
    lateinit var mdp_reconfirm: EditText
    lateinit var mdp:EditText
    lateinit var prenom : EditText;
    lateinit var nom:EditText;
    lateinit var mail:EditText;
    lateinit var adresse:EditText;
    var old_mdp = ""
    lateinit var nationalite:EditText;
    lateinit var date_naissance:EditText;
    lateinit var texte : EditText;
    lateinit var website:EditText;
    lateinit var cp_ville_pays:EditText;
    lateinit var tel:EditText;
    lateinit var sexe: Spinner
    var old_mail = ""
    var id = 0
    var showPwd = false;
    var showReconfirmPwd = false;
    var cp = ""
    var ville = ""
    var pays = ""
    var a = ""
    var ca = ""
    var s = ""
    var mp = ""
    lateinit var am : AccountManagement
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_profile)

        retour = findViewById(R.id.signup_to_login_btn_2)
        retour.setOnClickListener(this);
        valider = findViewById(R.id.signup_valid_btn_2);
        valider.setOnClickListener(this);
        display_mdp = findViewById(R.id.display_mdp_2);
        display_mdp.setOnClickListener(this)
        display_reconfirm_mdp = findViewById(R.id.display_mdp_reconfirm_2)
        display_reconfirm_mdp.setOnClickListener(this);
        mdp = findViewById(R.id.mdp_input_2)
        mdp_reconfirm = findViewById(R.id.mdp_reconfirm_input_2)
        sexe = findViewById(R.id.sexe_spinner_2)
        tel = findViewById(R.id.tel_input_2);
        prenom = findViewById(R.id.fn_input_2)
        nom=findViewById(R.id.ln_input_2);
        mail = findViewById(R.id.mail_input_2);
        adresse = findViewById(R.id.adresse_input_2)
        nationalite = findViewById(R.id.nationalite_input_2)
        date_naissance = findViewById(R.id.date_naissance_input_2)
        texte = findViewById(R.id.text_input_2)
        cp_ville_pays = findViewById(R.id.cp_ville_pays_input_2)
        website = findViewById(R.id.website_input_2)
        am = AccountManagement(this)
        AsyncLoad().execute(am.getLogin())
        old_mail = am.getLogin().toString()

    }

    fun regexCheck(re: Regex, str: String):Boolean{
        return re.matches(str)
    }

    override fun onClick(v: View) {
        when (v.getId()) {
            R.id.signup_valid_btn_2 -> {
                if(nom.text.toString() == ""||prenom.text.toString()==""||mail.text.toString()==""){
                    Toast.makeText(this,"Les champs avec * sont obligatoires",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mdp.text.toString()!=mdp_reconfirm.text.toString()){
                    Toast.makeText(this,"Les mots de passe ne correspondent pas.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mdp.text.toString() != "" && mdp.text.toString().length < 8){
                    Toast.makeText(this,"Le mot de passe doit contenir au moins 8 caractères.",Toast.LENGTH_SHORT).show();
                    return;
                }
                var regexD = Regex("^\\d{4}-\\d{2}-\\d{2}$")
                if (date_naissance.text.toString() != ""&& !regexCheck(regexD,date_naissance.text.toString())){
                    Toast.makeText(this,"Votre date de naissance ne respecte pas le format AAAA-MM-DD",Toast.LENGTH_SHORT).show();
                    return;
                }
                var part_cha = adresse.text.toString().split("/")
                if (adresse.text.toString() != "" && part_cha.size > 2){
                    Toast.makeText(this,"Merci de respecter le format [Adresse]/[Complément] car il y a trop d'arguments : "+part_cha.size+" > 2",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (adresse.text.toString() != ""){
                    a = part_cha[0]
                    if (part_cha.size == 2){
                        ca=part_cha[1]
                    }else{
                        ca=""
                    }
                } else{
                    a=""
                    ca=""
                }
                var part_champ = cp_ville_pays.text.toString().split('/')
                if (cp_ville_pays.text.toString() != "" && part_champ.size > 3){
                    Toast.makeText(this,"Merci de respecter le format [Code postal]/[Ville]/[Pays] car il y a trop d'arguments : "+part_champ.size+" > 3",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cp_ville_pays.text.toString() != ""){
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
                var regexTel = Regex("^\\d{10}$")
                var regexCp = Regex("^\\d{5}$")
                if (cp != "" && !regexCheck(regexCp,cp)){
                    Toast.makeText(this,"Le code postal doit être composé de 5 chiffres exactement",Toast.LENGTH_SHORT).show();
                    return
                }
                if (tel.text.toString() != "" && !regexCheck(regexTel,tel.text.toString())){
                    Toast.makeText(this,"Le numéro de téléphone doit être composé de 10 chiffres exactement",Toast.LENGTH_SHORT).show();
                    return
                }
                var regex1Web = Regex("^(https?://)?([\\w-]+(\\.[\\w-]+)+)(/[\\w-]*)*/?$")
                var regex2Web = Regex("^[\\w-]+(\\.[\\w-]+)+$")
                var regex3Web = Regex("^(http?://)?([\\w-]+(\\.[\\w-]+)+)(/[\\w-]*)*/?$")
                if (website.text.toString() != "" && (!regexCheck(regex1Web,website.text.toString()) || !regexCheck(regex2Web,website.text.toString()) || !regexCheck(regex3Web,website.text.toString()))){
                    Toast.makeText(this,"L'URL du site Web n'est pas valide.'",Toast.LENGTH_SHORT).show();
                    return
                }
                if (sexe.getSelectedItem().toString().contains("Homme")){
                    s = "M"
                } else if (sexe.getSelectedItem().toString().contains("Femme")){
                    s="F"
                }else {
                    s = ""
                }
                if (mdp.text.toString() != ""){
                    mp = mdp.text.toString()
                } else {
                    mp = old_mdp
                }
                Log.i("new_mdp",mp)
                if (old_mail != mail.text.toString()) {
                    AsyncCheckEmailUnique().execute(
                        s,
                        nom.text.toString(),
                        prenom.text.toString(),
                        mail.text.toString(),
                        date_naissance.text.toString(),
                        mp,
                        nationalite.text.toString(),
                        texte.text.toString(),
                        a,
                        ca,
                        cp,
                        ville,
                        pays,
                        tel.text.toString(),
                        website.text.toString()
                    )
                } else {
                    AsyncUpdateAccount().execute(
                        s,
                        nom.text.toString(),
                        prenom.text.toString(),
                        mail.text.toString(),
                        date_naissance.text.toString(),
                        mp,
                        nationalite.text.toString(),
                        texte.text.toString(),
                        a,
                        ca,
                        cp,
                        ville,
                        pays,
                        tel.text.toString(),
                        website.text.toString()
                    )
                }
            }
            R.id.signup_to_login_btn_2 -> {
                finish()
                startActivity(Intent(this,ProfileActivity::class.java))
            }
            R.id.display_mdp_2 -> {
                if (showPwd) {
                    mdp.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    showPwd = false;
                } else {
                    mdp.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    showPwd = true;
                }
            }
            R.id.display_mdp_reconfirm_2 -> {
                if (showReconfirmPwd) {
                    mdp_reconfirm.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    showReconfirmPwd = false;
                } else {
                    mdp_reconfirm.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    showReconfirmPwd = true;
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        startActivity(Intent(this,ProfileActivity::class.java))
    }

    private inner class AsyncLoad : AsyncTask<String, Void, String>() {
        private val pdLoading = ProgressDialog(this@ModifyProfileActivity)
        private var co: HttpURLConnection? = null
        private var url: URL? = null
        override fun onPreExecute() {
            super.onPreExecute()

            pdLoading.setMessage("Chargement des informations...")
            pdLoading.setCancelable(false)
            pdLoading.show()
        }

        override fun doInBackground(vararg strings: String?): String {
            try {
                url = URL("http://10.0.2.2:8000/api/compte/find-by-email")
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
                    .appendQueryParameter("email", strings[0])

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
            Log.d("message", result)
            try {
                val json = JSONObject(result)
                val found = json.getBoolean("found")
                val compte = json.getJSONObject("compte")
                val sexe = compte.getString("sexe")
                if (found) {
                    old_mdp = (compte.optString("mdp").takeIf { it != "null" } ?: "")
                    Log.i("old_mdp",old_mdp)
                    id=compte.optInt("id")
                    nom.setText((compte.optString("nom").takeIf { it != "null" } ?: ""))
                    prenom.setText((compte.optString("prenom").takeIf { it != "null" } ?: ""))
                    mail.setText((compte.optString("email").takeIf { it != "null" } ?: ""))
                    date_naissance.setText((compte.optString("date_naissance").takeIf { it != "null" } ?: ""))
                    nationalite.setText((compte.optString("nationalite").takeIf { it != "null" } ?: ""))
                    texte.setText((compte.optString("titre").takeIf { it != "null" } ?: ""))
                    website.setText((compte.optString("website").takeIf { it != "null" } ?: ""))
                    if ((compte.optString("adresse").takeIf { it != "null" } ?: "") != "") {
                        adresse.setText((compte.optString("adresse").takeIf { it != "null" }
                            ?: ""))
                        if ((compte.optString("adresse_comp").takeIf { it != "null" } ?: "") != ""){
                            adresse.setText((compte.optString("adresse").takeIf { it != "null" }
                                ?: "")+"/"+(compte.optString("adresse_comp").takeIf { it != "null" }
                                ?: ""))
                        }
                    } else {
                        if ((compte.optString("adresse_comp").takeIf { it != "null" } ?: "") != ""){
                            adresse.setText("/"+(compte.optString("adresse_comp").takeIf { it != "null" }
                                ?: ""))
                        }
                    }
                    if ((compte.optString("cp").takeIf { it != "null" } ?: "") != ""){
                        cp_ville_pays.setText((compte.optString("cp").takeIf { it != "null" } ?: ""))
                        if ((compte.optString("ville").takeIf { it != "null" } ?: "") != ""){
                            cp_ville_pays.setText((compte.optString("cp").takeIf { it != "null" } ?: "") + "/" + (compte.optString("ville").takeIf { it != "null" } ?: ""))
                            if ((compte.optString("pays").takeIf { it != "null" } ?: "") != "") {
                                cp_ville_pays.setText((compte.optString("cp").takeIf { it != "null" } ?: "") + "/" + (compte.optString("ville").takeIf { it != "null" } ?: "")+"/" + (compte.optString("pays").takeIf { it != "null" } ?: ""))
                            }
                        } else {
                            cp_ville_pays.setText((compte.optString("cp").takeIf { it != "null" } ?: "") + "//")
                            if ((compte.optString("pays").takeIf { it != "null" } ?: "") != "") {
                                cp_ville_pays.setText((compte.optString("cp").takeIf { it != "null" } ?: "") + "//" + (compte.optString("pays").takeIf { it != "null" } ?: ""))
                            }
                        }
                    } else {
                        if ((compte.optString("ville").takeIf { it != "null" } ?: "") != "" || (compte.optString("pays").takeIf { it != "null" } ?: "") != ""){
                            cp_ville_pays.setText("/")
                            if ((compte.optString("ville").takeIf { it != "null" } ?: "") != ""){
                                cp_ville_pays.setText("/"+(compte.optString("ville").takeIf { it != "null" } ?: "")+"/")
                            } else {
                                if ((compte.optString("pays").takeIf { it != "null" } ?: "") != ""){
                                    cp_ville_pays.setText("//"+(compte.optString("pays").takeIf { it != "null" } ?: ""))
                                }
                            }
                        }
                    }
                    tel.setText((compte.optString("numero").takeIf { it != "null" } ?: ""))
                    /*if (sexe == "M"){
                        sexe_text.setText(sexe_text.text.toString() + "Homme")
                    } else if (sexe == "F"){
                        sexe_text.setText(sexe_text.text.toString() + "Femme")
                    }



                    */
                }else {
                    Toast.makeText(
                        this@ModifyProfileActivity,
                        "Nous n'avons pas pu charger vos informations.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ModifyProfileActivity,
                    "Erreur de parsing JSON. Retour au menu précédent",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    private inner class AsyncUpdateAccount : AsyncTask<String, Void, String>() {
        private val pdLoading = ProgressDialog(this@ModifyProfileActivity)
        private var co: HttpURLConnection? = null
        private var url: URL? = null
        private var emailUser: String? = null
        private var prenomA : String? = null
        override fun onPreExecute() {
            super.onPreExecute()

            pdLoading.setMessage("Modification de vos informations...")
            pdLoading.setCancelable(false)
            pdLoading.show()
        }

        override fun doInBackground(vararg strings: String?): String {
            try {
                url = URL("http://10.0.2.2:8000/api/compte/"+id)
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

                prenomA = strings[2]
                emailUser = strings[3]
                val builder = Uri.Builder()
                    .appendQueryParameter("sexe", strings[0])
                    .appendQueryParameter("nom", strings[1])
                    .appendQueryParameter("prenom", strings[2])
                    .appendQueryParameter("email", strings[3])
                    .appendQueryParameter("date_naissance", strings[4])
                    .appendQueryParameter("mdp", strings[5])
                    .appendQueryParameter("nationalite", strings[6])
                    .appendQueryParameter("titre", strings[7])
                    .appendQueryParameter("address", strings[8])
                    .appendQueryParameter("address_comp", strings[9])
                    .appendQueryParameter("cp", strings[10])
                    .appendQueryParameter("ville", strings[11])
                    .appendQueryParameter("pays", strings[12])
                    .appendQueryParameter("numero", strings[13])
                    .appendQueryParameter("website", strings[14])

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
                Log.i("a","a")
                if (success) {
                    Log.i("a","b")
                    if (mail.text.toString() != am.getLogin()){
                        Toast.makeText(this@ModifyProfileActivity,"Compte modifié avec succès ! Comme vous avez modifié votre adresse mail, vous allez recevoir un mail à cette nouvelle adresse mail.",Toast.LENGTH_LONG).show()
                        am.updateInfo(mail.text.toString())
                        AsyncMailAuto().execute(mail.text.toString(),"Modification de votre adresse email","Bonjour,<br /><br />Votre compte vient de subir la modification de votre adresse mail.<br />Adresse mail : <s>"+old_mail+"</s>  "+mail.text.toString()+".<br /><br />Cordialement, <br />L'équipe de suivi des candidatures")
                    } else {
                        Toast.makeText(this@ModifyProfileActivity,"Compte modifié avec succès !",Toast.LENGTH_LONG).show()
                    }
                    finish()
                    startActivity(Intent(this@ModifyProfileActivity,ProfileActivity::class.java))
                } else {
                    Log.i("b","a")
                    Toast.makeText(
                        this@ModifyProfileActivity,
                        "Echec d'inscription",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ModifyProfileActivity,
                    "Erreur de parsing JSON",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private inner class AsyncMailAuto : AsyncTask<String, Void, String>() {
        private var co: HttpURLConnection? = null
        private var url: URL? = null
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg strings: String?): String {
            try {
                url = URL("http://10.0.2.2:8000/api/test-mail")
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
                    .appendQueryParameter("email", strings[0])
                    .appendQueryParameter("subject", strings[1])
                    .appendQueryParameter("content", strings[2])

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

            if (result == null) return
            Log.d("message", result)
            try {
                val json = JSONObject(result)
                val success = json.getBoolean("success")
                if (success) {
                    Toast.makeText(this@ModifyProfileActivity,"Mail automatique envoyé",Toast.LENGTH_SHORT)
                } else {
                    Toast.makeText(
                        this@ModifyProfileActivity,
                        "Echec d'envoi du mail",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ModifyProfileActivity,
                    "Erreur de parsing JSON",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private inner class AsyncCheckEmailUnique : AsyncTask<String, Void, String>() {
        private val pdLoading = ProgressDialog(this@ModifyProfileActivity)
        private var co: HttpURLConnection? = null
        private var url: URL? = null
        private var sex: String? = null
        private var n: String? = null
        private var pr: String? = null
        private var emailUser: String? = null
        private var nation: String? = null
        private var pwd: String? = null
        private var adr: String? = null
        private var adrc: String? = null
        private var v: String? = null
        private var code: String? = null
        private var tel: String? = null
        private var pa: String? = null
        private var w: String? = null
        private var dn: String? = null
        private var title: String? = null
        override fun onPreExecute() {
            super.onPreExecute()

            pdLoading.setMessage("Vérification email unique.")
            pdLoading.setCancelable(false)
            pdLoading.show()
        }

        override fun doInBackground(vararg strings: String?): String {
            try {
                url = URL("http://10.0.2.2:8000/api/compte/find-by-email")
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
                sex = strings[0]
                n = strings[1]
                pr = strings[2]
                emailUser = strings[3]
                pwd = strings[5]
                dn = strings[4]
                adr = strings[8]
                title = strings[7]
                adrc = strings[9]
                nation = strings[6]
                code = strings[10]
                v = strings[11]
                pa = strings[12]
                tel = strings[13]
                w = strings[14]

                val builder = Uri.Builder()
                    .appendQueryParameter("email", emailUser)

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
            Log.d("message", result)
            try {
                val json = JSONObject(result)
                val found = json.getBoolean("found")
                if (found) {
                    Toast.makeText(
                        this@ModifyProfileActivity,
                        "Un compte avec cette adresse email existe déjà. Veuillez utiliser une adresse email différente.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    AsyncUpdateAccount().execute(sex,n,pr,emailUser,dn,pwd,nation,title,adr,adrc,code,v,pa,tel,w)
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ModifyProfileActivity,
                    "Erreur de parsing JSON",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}