package com.example.suivicandidatures

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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

class ProfileActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var retour : Button
    lateinit var modifier : Button
    lateinit var cv : Button
    lateinit var logout : Button
    lateinit var da : Button
    lateinit var sexe_text : TextView
    lateinit var nom : TextView
    lateinit var prenom : TextView
    lateinit var email : TextView
    lateinit var date_naissance : TextView
    lateinit var mdp : TextView
    lateinit var nationalite : TextView
    lateinit var texte : TextView
    lateinit var adresse : TextView
    lateinit var adresse_comp : TextView
    lateinit var cp : TextView
    lateinit var ville : TextView
    lateinit var pays : TextView
    lateinit var numtel : TextView
    lateinit var web : TextView
    lateinit var date_creation : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        retour = findViewById(R.id.profile_to_menu_button)
        retour.setOnClickListener(this)
        modifier = findViewById(R.id.profile_to_form_button)
        modifier.setOnClickListener(this)
        cv = findViewById(R.id.profile_to_cvs_button)
        cv.setOnClickListener(this)
        logout = findViewById(R.id.profile_to_confirm_logout_button)
        logout.setOnClickListener(this)
        da = findViewById(R.id.profile_to_confirm_da_button)
        da.setOnClickListener(this)
        sexe_text = findViewById(R.id.sexe_text)
        nom = findViewById(R.id.ln_text)
        prenom = findViewById(R.id.fn_text)
        email = findViewById(R.id.email_text)
        mdp = findViewById(R.id.mdp_text)
        date_naissance = findViewById(R.id.dn_text)
        date_creation = findViewById(R.id.date_creation_text)
        nationalite = findViewById(R.id.nationalite_text)
        web = findViewById(R.id.website_text)
        numtel = findViewById(R.id.tel_text)
        pays = findViewById(R.id.pays_text)
        texte = findViewById(R.id.titre_text)
        adresse = findViewById(R.id.adresse_text)
        adresse_comp = findViewById(R.id.adresse_comp_text)
        cp = findViewById(R.id.code_postal_text)
        ville = findViewById(R.id.ville_text)
        var a = AccountManagement(this)
        AsyncLoad().execute(a.getLogin())
    }

    override fun onClick(v: View) {
        when (v.getId()){
            R.id.profile_to_menu_button -> {
                finish()
            }
            R.id.profile_to_confirm_logout_button -> {
                startActivity(Intent(this, ConfirmLogoutActivity::class.java))
            }
            R.id.profile_to_confirm_da_button -> {
                startActivity(Intent(this,ConfirmDeleteAccountActivity::class.java))
            }
            R.id.profile_to_cvs_button -> {

            }
            R.id.profile_to_form_button -> {
                startActivity(Intent(this,ModifyProfileActivity::class.java))
            }
            else -> {

            }
        }
    }

    private inner class AsyncLoad : AsyncTask<String, Void, String>() {
        private val pdLoading = ProgressDialog(this@ProfileActivity)
        private var co: HttpURLConnection? = null
        private var url: URL? = null
        override fun onPreExecute() {
            super.onPreExecute()

            pdLoading.setMessage("Chargement...")
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
                    if (sexe == "M"){
                        sexe_text.setText(sexe_text.text.toString() + "Homme")
                    } else if (sexe == "F"){
                        sexe_text.setText(sexe_text.text.toString() + "Femme")
                    }
                    nom.setText(nom.text.toString() + (compte.optString("nom").takeIf { it != "null" } ?: ""))
                    prenom.setText(prenom.text.toString() + (compte.optString("prenom").takeIf { it != "null" } ?: ""))
                    email.setText(email.text.toString()+ (compte.optString("email").takeIf { it != "null" } ?: ""))
                    for (i in 0 until compte.getString("mdp").length){
                        mdp.setText(mdp.text.toString() + "*")
                    }
                    date_naissance.setText(date_naissance.text.toString() + (compte.optString("date_naissance").takeIf { it != "null" } ?: ""))
                    nationalite.setText(nationalite.text.toString() + (compte.optString("nationalite").takeIf { it != "null" } ?: ""))
                    texte.setText(texte.text.toString() + (compte.optString("titre").takeIf { it != "null" } ?: ""))
                    adresse.setText(adresse.text.toString() + (compte.optString("adresse").takeIf { it != "null" } ?: ""))
                    adresse_comp.setText(adresse_comp.text.toString() + (compte.optString("adresse_comp").takeIf { it != "null" } ?: ""))
                    cp.setText(cp.text.toString() + (compte.optString("cp").takeIf { it != "null" } ?: ""))
                    ville.setText(ville.text.toString() + (compte.optString("ville").takeIf { it != "null" } ?: ""))
                    pays.setText(pays.text.toString() + (compte.optString("pays").takeIf { it != "null" } ?: ""))
                    numtel.setText(numtel.text.toString() + (compte.optString("numero").takeIf { it != "null" } ?: ""))
                    web.setText(web.text.toString() + (compte.optString("website").takeIf { it != "null" } ?: ""))
                    date_creation.setText(date_creation.text.toString() + (compte.optString("created_at").takeIf { it != "null" } ?: "").substring(0,10))
                }else {
                    Toast.makeText(
                        this@ProfileActivity,
                        "Nous n'avons pas pu charger vos informations.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ProfileActivity,
                    "Erreur de parsing JSON",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}