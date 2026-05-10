package com.example.suivicandidatures

import android.annotation.SuppressLint
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

class MainMenuActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var candidatures : Button
    lateinit var profile : Button
    lateinit var deconnect : Button
    lateinit var title : TextView
    lateinit var login : String
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        title = findViewById(R.id.titlemenu)
        candidatures = findViewById(R.id.menu_to_candidatures_button)
        candidatures.setOnClickListener(this)
        profile = findViewById(R.id.menu_to_profil_button)
        profile.setOnClickListener(this)
        deconnect = findViewById(R.id.deconnect_button)
        deconnect.setOnClickListener(this)
        val am = AccountManagement(this)
        login = am.getLogin().toString()
        Log.i("login",login)
        AsyncLoad().execute(login)
    }

    override fun onClick(v: View) {
        when (v.getId()){
            R.id.menu_to_profil_button -> {
                startActivity(Intent(this,ProfileActivity::class.java))
            }
            R.id.deconnect_button -> {
                startActivity(Intent(this,ConfirmLogoutActivity::class.java))
            }
            R.id.menu_to_candidatures_button -> {

            }
            else -> {

            }
        }
    }

    private inner class AsyncLoad : AsyncTask<String, Void, String>() {
        private val pdLoading = ProgressDialog(this@MainMenuActivity)
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
                val prenom = compte.getString("prenom")
                if (found) {
                    title.setText("Bienvenue, "+prenom+" !")
                }else {
                    Toast.makeText(
                        this@MainMenuActivity,
                        "Nous n'avons pas trouvé votre compte dans la base de données. Retour à la connexion.",
                        Toast.LENGTH_LONG
                    ).show()
                    val a = AccountManagement(this@MainMenuActivity)
                    a.logout()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainMenuActivity,
                    "Erreur de parsing JSON",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}