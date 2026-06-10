package com.example.suivicandidatures

import android.annotation.SuppressLint
import android.app.ProgressDialog
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
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ConfirmDeleteCVActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var oui : Button
    lateinit var non : Button
    lateinit var question : TextView
    lateinit var extras : Bundle
    var id = 0
    var nom = ""
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_delete_cvactivity)
        oui = findViewById(R.id.delete_cv_button)
        oui.setOnClickListener(this)
        non = findViewById(R.id.no_delete_cv_button)
        non.setOnClickListener(this)
        question = findViewById(R.id.titleconfirmdeletecv)
        extras = intent.extras!!
        id = extras.getInt("id_cv")
        nom = extras.getString("nom_cv").toString()
        question.setText("Êtes-vous sûr de vouloir supprimer le CV \""+ id + ". " + nom + "\" ?")
    }

    override fun onClick(v: View) {
        when (v.getId()){
            R.id.delete_cv_button -> {
                AsyncDeleteCV().execute()
            }
            R.id.no_delete_cv_button -> {
                finish()
            }
            else -> {

            }
        }
    }

    private inner class AsyncDeleteCV : AsyncTask<Void, Void, String>() {
        private val pdLoading = ProgressDialog(this@ConfirmDeleteCVActivity)
        override fun onPreExecute() {
            super.onPreExecute()

            pdLoading.setMessage("Suppression du CV...")
            pdLoading.setCancelable(false)
            pdLoading.show()
        }

        override fun doInBackground(vararg params: Void?): String {
            try {
                /*
                 * =========================
                 * Récupération candidatures
                 * =========================
                 */

                var url = URL("http://10.0.2.2:8000/api/cv/"+id+"/candidatures")
                var co = url.openConnection() as HttpURLConnection
                co.requestMethod = "GET"
                co.setRequestProperty("Content-Type", "application/json")
                val candidaturesResponse = if (co.responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(co.inputStream))
                    val result = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        result.append(line)
                    }
                    result.toString()
                } else {
                    return "Erreur récupération candidatures"
                }

                co.disconnect()

                val candidaturesArray = JSONObject("{\"data\":$candidaturesResponse}")
                    .getJSONArray("data")

                /*
                 * =========================
                 * Suppression candidatures
                 * =========================
                 */

                for (i in 0 until candidaturesArray.length()) {
                    val candidature = candidaturesArray.getJSONObject(i)
                    val candidatureId = candidature.getInt("id")
                    url = URL("http://10.0.2.2:8000/api/candidature/$candidatureId")
                    co = url.openConnection() as HttpURLConnection
                    co.requestMethod = "DELETE"
                    co.setRequestProperty("Content-Type", "application/json")
                    co.responseCode
                    co.disconnect()
                }


                url = URL("http://10.0.2.2:8000/api/cv/"+id)

                co = url.openConnection() as HttpURLConnection

                co.requestMethod = "DELETE"
                co.setRequestProperty("Content-Type", "application/json")

                return if (co.responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(co.inputStream))
                    val result = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        result.append(line)
                    }
                    co.disconnect()
                    result.toString()
                } else {
                    co.disconnect()
                    "Erreur suppression CV"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return "Exception"
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            pdLoading.dismiss()
            if (result == null) return
            Log.d("DELETE_RESULT", result)
            try {
                val json = JSONObject(result)
                val success = json.getBoolean("success")
                if (success) {
                    Toast.makeText(
                        this@ConfirmDeleteCVActivity,
                        "CV supprimé avec succès",
                        Toast.LENGTH_LONG
                    ).show()
                    setResult(RESULT_OK)
                    finish()
                } else {

                    Toast.makeText(
                        this@ConfirmDeleteCVActivity,
                        "Échec de suppression du CV",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ConfirmDeleteCVActivity,
                    "Erreur de parsing JSON",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}