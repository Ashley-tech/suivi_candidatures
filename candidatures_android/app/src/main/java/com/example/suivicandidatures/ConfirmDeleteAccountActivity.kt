package com.example.suivicandidatures

import android.app.ProgressDialog
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
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

class ConfirmDeleteAccountActivity : AppCompatActivity(),View.OnClickListener {
    lateinit var oui : Button
    lateinit var non : Button
    lateinit var extras : Bundle
    var id = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_delete_account)
        oui = findViewById(R.id.delete_account_button)
        non = findViewById(R.id.no_delete_account_button)
        oui.setOnClickListener(this)
        non.setOnClickListener(this)
        extras = intent.extras!!
        id = extras.getInt("id")
        Log.d("id r", id.toString())
    }

    override fun onClick(v: View) {
        when (v.getId()){
            R.id.delete_account_button -> {
                AsyncDeleteAccount().execute()
            }
            R.id.no_delete_account_button -> {
                finish()
            }
            else -> {

            }
        }
    }

    private inner class AsyncDeleteAccount : AsyncTask<Void, Void, String>() {
        private val pdLoading = ProgressDialog(this@ConfirmDeleteAccountActivity)
        override fun onPreExecute() {
            super.onPreExecute()

            pdLoading.setMessage("Suppression du compte...")
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

                var url = URL("http://10.0.2.2:8000/api/compte/"+id+"/candidatures")
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

                /*
                 * =========================
                 * Récupération CVs
                 * =========================
                 */

                url = URL("http://10.0.2.2:8000/api/compte/"+id+"/cvs")

                co = url.openConnection() as HttpURLConnection

                co.requestMethod = "GET"
                co.setRequestProperty("Content-Type", "application/json")

                val cvsResponse = if (co.responseCode == HttpURLConnection.HTTP_OK) {

                    val reader = BufferedReader(InputStreamReader(co.inputStream))
                    val result = StringBuilder()

                    var line: String?

                    while (reader.readLine().also { line = it } != null) {
                        result.append(line)
                    }

                    result.toString()

                } else {
                    return "Erreur récupération CVs"
                }

                co.disconnect()

                val cvsArray = JSONObject("{\"data\":$cvsResponse}")
                    .getJSONArray("data")

                /*
                 * =========================
                 * Suppression CVs
                 * =========================
                 */

                for (i in 0 until cvsArray.length()) {

                    val cv = cvsArray.getJSONObject(i)

                    val cvId = cv.getInt("id")

                    url = URL("http://10.0.2.2:8000/api/cv/$cvId")

                    co = url.openConnection() as HttpURLConnection

                    co.requestMethod = "DELETE"
                    co.setRequestProperty("Content-Type", "application/json")

                    co.responseCode

                    co.disconnect()
                }

                /*
                 * =========================
                 * Suppression compte
                 * =========================
                 */

                url = URL("http://10.0.2.2:8000/api/compte/"+id)

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
                    "Erreur suppression compte"
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
                        this@ConfirmDeleteAccountActivity,
                        "Compte supprimé avec succès",
                        Toast.LENGTH_LONG
                    ).show()
                    val am = AccountManagement(this@ConfirmDeleteAccountActivity)
                    am.deleteAccount()
                } else {

                    Toast.makeText(
                        this@ConfirmDeleteAccountActivity,
                        "Échec de suppression du compte",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ConfirmDeleteAccountActivity,
                    "Erreur de parsing JSON",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}