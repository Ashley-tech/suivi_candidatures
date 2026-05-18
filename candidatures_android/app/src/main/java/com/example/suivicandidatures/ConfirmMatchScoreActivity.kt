package com.example.suivicandidatures

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.util.Log
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ConfirmMatchScoreActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var oui : Button
    lateinit var non : Button
    lateinit var extras : Bundle
    var c = 0
    var old = 0.00
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_match_score)
        oui = findViewById(R.id.match_cv_button)
        non = findViewById(R.id.no_match_cv_button)
        oui.setOnClickListener(this)
        non.setOnClickListener(this)
        extras = intent.extras!!
        c = extras.getInt("id_candidature")
        old = extras.getDouble("old_score")
        Log.i("old_score",old.toString())
    }

    override fun onClick(v: View) {
        when (v.getId()) {
            R.id.match_cv_button -> {
                AsyncMatchScore().execute()
            }

            R.id.no_match_cv_button -> {
                finish()
            }
            else -> {

            }
        }
    }

    private inner class AsyncMatchScore : AsyncTask<Void, Void, String>() {
        private val pdLoading = ProgressDialog(this@ConfirmMatchScoreActivity)
        private var co: HttpURLConnection? = null
        private var url: URL? = null

        override fun onPreExecute() {
            super.onPreExecute()

            pdLoading.setMessage("Matching de la candidature...")
            pdLoading.setCancelable(false)
            pdLoading.show()
        }

        override fun doInBackground(vararg params: Void?): String {
            try {

                /*
                 * Récupération des candidatures
                 */

                url = URL("http://10.0.2.2:8000/api/candidature/$c/save-score")

                co = url!!.openConnection() as HttpURLConnection

                co!!.readTimeout = 15000
                co!!.connectTimeout = 15000
                co!!.requestMethod = "PATCH"

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
            var j = JSONObject(result)

            try {
                Toast.makeText(this@ConfirmMatchScoreActivity,"Score de matching recalculé avec succès : " + (j.optString("score").takeIf { it != "null" } ?: "") + "%. Ancien score : "+old+" %", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } catch (e: Exception) {
                e.printStackTrace()

                Toast.makeText(
                    this@ConfirmMatchScoreActivity,
                    "Erreur de parsing JSON",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}