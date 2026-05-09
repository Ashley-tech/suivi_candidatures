package com.example.suivicandidatures

import android.app.ProgressDialog
import android.graphics.Color.rgb
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
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

class ForgotActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var resultat : TextView
    lateinit var mail_input: EditText
    lateinit var retour: Button
    lateinit var verifier: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)
        resultat = findViewById(R.id.result_forgot)
        mail_input = findViewById(R.id.mail_input_forgot)
        retour = findViewById(R.id.forgot_to_login_btn)
        retour.setOnClickListener(this);
        verifier = findViewById(R.id.receive_link_btn)
        verifier.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.getId()){
            R.id.forgot_to_login_btn -> {
                finish()
            }
            R.id.receive_link_btn ->  {
                if (mail_input.text.toString() == ""){
                    resultat.text = "Ce champ est obligatoire !"
                    resultat.setTextColor(rgb(255,0,0))
                } else {
                    AsyncCheck().execute(mail_input.text.toString())
                }
            }
            else -> {

            }
        }
    }

    private inner class AsyncCheck : AsyncTask<String, Void, String>() {
        private val pdLoading = ProgressDialog(this@ForgotActivity)
        private var co: HttpURLConnection? = null
        private var url: URL? = null
        private var idc: String? = null
        private var emailUser: String? = null
        override fun onPreExecute() {
            super.onPreExecute()

            pdLoading.setMessage("Vérification de votre adresse mail...")
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

                emailUser = strings[0]
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
                val compte = json.getJSONObject("compte")
                idc = compte.getInt("id").toString()
                Log.d("id", idc.toString())
                if (found) {
                    resultat.text = "Email trouvé ! Un mail va vous être envoyé avec les instructions pour réinitialiser votre mot de passe. Vous serz obligé de basculer sur le site web pour changer votre mot de passe."
                    resultat.setTextColor(rgb(0,255,0))
                    AsyncMailAuto().execute(emailUser,"Réinitialisation de votre mot de passe","Bonjour,<br /><br />Voici le <a href='http://127.0.0.1:8000/"+idc.toString()+"/new_password'>lien</a> pour réinitialiser votre mot de passe.<br /><br />Cordialement, <br />L'équipe de suivi des candidatures");
                } else {
                    resultat.text="Email non trouvé."
                    resultat.setTextColor(rgb(255,0,0))
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ForgotActivity,
                    "Erreur de parsing JSON",
                    Toast.LENGTH_LONG
                ).show()
                resultat.text = "Une erreur s'est produite"
            }
        }
    }

    private inner class AsyncMailAuto : AsyncTask<String, Void, String>() {
        private val pdLoading = ProgressDialog(this@ForgotActivity)
        private var co: HttpURLConnection? = null
        private var url: URL? = null
        override fun onPreExecute() {
            super.onPreExecute()

            //pdLoading.setMessage("Inscription.")
            //pdLoading.setCancelable(false)
            //pdLoading.show()
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
            pdLoading.dismiss()

            if (result == null) return
            Log.d("message", result)
            try {
                val json = JSONObject(result)
                val success = json.getBoolean("success")
                if (success) {
                    Toast.makeText(this@ForgotActivity,"Mail automatique envoyé",Toast.LENGTH_SHORT)
                } else {
                    Toast.makeText(
                        this@ForgotActivity,
                        "Echec d'envoi du mail",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ForgotActivity,
                    "Erreur de parsing JSON",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}