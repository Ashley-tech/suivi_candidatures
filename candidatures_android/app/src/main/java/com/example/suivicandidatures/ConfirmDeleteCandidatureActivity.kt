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

class ConfirmDeleteCandidatureActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var oui : Button
    lateinit var non : Button
    lateinit var question : TextView
    lateinit var extras : Bundle
    var id = 0
    var offre = 0
    var previousActivity = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_delete_candidature)
        oui = findViewById(R.id.delete_candidature_button)
        oui.setOnClickListener(this)
        non = findViewById(R.id.no_delete_candidature_button)
        non.setOnClickListener(this)
        question = findViewById(R.id.titleconfirmdeletecandidature)
        extras = intent.extras!!
        id = extras.getInt("id_candidature")
        offre = extras.getInt("id_offre")
        previousActivity = extras.getString("previous_activity").toString()
        AsyncLoad().execute()
    }

    override fun onClick(v: View) {
        when (v.getId()){
            R.id.delete_candidature_button -> {
                AsyncDelete().execute()
            }
            R.id.no_delete_candidature_button -> {
                finish()
            }
            else -> {

            }
        }
    }

    private inner class AsyncLoad : AsyncTask<String, Void, String>() {
        private var co: HttpURLConnection? = null
        private var url: URL? = null
        override fun onPreExecute() {
            super.onPreExecute()
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
                co!!.requestMethod = "GET"
                co!!.setRequestProperty(
                    "Content-Type",
                    "application/x-www-form-urlencoded"
                )

                co!!.doInput = true

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
                val nom = json.getString("titre")
                question.setText("Êtes-vous sûr de vouloir supprimer votre candidature n°" + id + " en tant que "+nom+" ?")
            } catch (e: Exception) {
                Toast.makeText(
                    this@ConfirmDeleteCandidatureActivity,
                    "Erreur de parsing JSON",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private inner class AsyncDelete : AsyncTask<String, Void, String>() {
        private var co: HttpURLConnection? = null
        private var url: URL? = null
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg strings: String?): String {
            try {
                url = URL("http://10.0.2.2:8000/api/candidature/"+id)
            } catch (e: MalformedURLException) {
                e.printStackTrace()
                return "false"
            }

            try {
                co = url!!.openConnection() as HttpURLConnection
                co!!.readTimeout = 15000
                co!!.connectTimeout = 15000
                co!!.requestMethod = "DELETE"
                co!!.setRequestProperty(
                    "Content-Type",
                    "application/x-www-form-urlencoded"
                )

                co!!.doInput = true

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
                if (success){
                    Toast.makeText(
                        this@ConfirmDeleteCandidatureActivity,
                        "Candidature supprimé avec succès",
                        Toast.LENGTH_LONG
                    ).show()
                    if (previousActivity == "InfoCandidatureActivity") {
                        val intent = Intent(
                            this@ConfirmDeleteCandidatureActivity,
                            CandidaturesActivity::class.java
                        )
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        setResult(RESULT_OK)
                        startActivity(intent)
                        finish()
                    } else {
                        setResult(RESULT_OK)
                        finish()
                    }
                } else {
                    Toast.makeText(
                        this@ConfirmDeleteCandidatureActivity,
                        "Erreur lors de la suppression de la candidature",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ConfirmDeleteCandidatureActivity,
                    "Erreur de parsing JSON",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}