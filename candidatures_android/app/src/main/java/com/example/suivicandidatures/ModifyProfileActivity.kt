package com.example.suivicandidatures

import android.app.ProgressDialog
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
    lateinit var nationalite:EditText;
    lateinit var date_naissance:EditText;
    lateinit var texte : EditText;
    lateinit var website:EditText;
    lateinit var cp_ville_pays:EditText;
    lateinit var tel:EditText;
    lateinit var sexe: Spinner
    var showPwd = false;
    var showReconfirmPwd = false;
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
        var a = AccountManagement(this)
        AsyncLoad().execute(a.getLogin())
    }

    override fun onClick(v: View) {
        when (v.getId()) {
            R.id.signup_valid_btn_2 -> {

            }
            R.id.signup_to_login_btn_2 -> {
                finish()
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
}