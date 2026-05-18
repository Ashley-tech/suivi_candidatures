package com.example.suivicandidatures

import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class InfoCandidatureActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var extras : Bundle
    lateinit var delete : Button
    lateinit var modifier : Button
    lateinit var matcher : Button
    lateinit var retour : Button
    lateinit var type : TextView
    lateinit var details : TextView
    lateinit var entreprise : TextView
    lateinit var localisation : TextView
    lateinit var recruteur : TextView
    lateinit var contact : TextView
    lateinit var periode : TextView
    lateinit var salaire : TextView
    lateinit var date_publication : TextView
    lateinit var date_candidature : TextView
    lateinit var statut : TextView
    lateinit var score_m : TextView
    lateinit var titre : TextView
    var c = 0
    var offre = 0
    var score: Double? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_candidature)
        delete = findViewById(R.id.info_to_delete_button)
        delete.setOnClickListener(this)
        modifier = findViewById(R.id.info_to_modify_candidature_button)
        modifier.setOnClickListener(this)
        matcher = findViewById(R.id.calculate_matching_score_button)
        matcher.setOnClickListener(this)
        retour = findViewById(R.id.info_to_candidatures_button)
        retour.setOnClickListener(this)
        titre = findViewById(R.id.titleinfocandidature)
        type = findViewById(R.id.type_text)
        details = findViewById(R.id.details_text)
        details.movementMethod = ScrollingMovementMethod()
        entreprise = findViewById(R.id.entreprise_text)
        localisation = findViewById(R.id.localisation_text)
        recruteur = findViewById(R.id.recruteur_text)
        contact = findViewById(R.id.contact_recruteur)
        periode = findViewById(R.id.periode_text)
        salaire = findViewById(R.id.salaire_text)
        date_candidature = findViewById(R.id.candidature_text)
        date_publication = findViewById(R.id.publication_text)
        statut = findViewById(R.id.statut_text)
        score_m = findViewById(R.id.score_matching_text)
        extras = intent.extras!!
        c = extras.getInt("id_candidature")
        offre = extras.getInt("id_offre")
        AsyncLoadOffer().execute()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {
            AsyncLoadOffer().execute()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(RESULT_OK)
        finish()
    }

    override fun onClick(v: View) {
        when (v.getId()) {
            R.id.info_to_delete_button -> {
                var intent = Intent(this, ConfirmDeleteCandidatureActivity::class.java)
                intent.putExtra("id_candidature", c)
                intent.putExtra("id_offre", offre)
                intent.putExtra("previous_activity", "InfoCandidatureActivity")
                startActivity(intent)
            }
            R.id.info_to_modify_candidature_button -> {

            }
            R.id.calculate_matching_score_button -> {
                if (score == null){
                    AsyncMatchScore().execute()
                } else {
                    val intent = Intent(this,ConfirmMatchScoreActivity::class.java)
                    intent.putExtra("id_candidature",c)
                    intent.putExtra("old_score", score)
                    startActivityForResult(intent,1)
                }
            }
            R.id.info_to_candidatures_button -> {
                setResult(RESULT_OK)
                finish()
            }
            else -> {

            }
        }
    }

    private inner class AsyncLoadOffer : AsyncTask<Void, Void, String>() {
        private val pdLoading = ProgressDialog(this@InfoCandidatureActivity)
        private var co: HttpURLConnection? = null
        private var url: URL? = null

        override fun onPreExecute() {
            super.onPreExecute()

            pdLoading.setMessage("Chargement de l'offre...")
            pdLoading.setCancelable(false)
            pdLoading.show()
        }

        override fun doInBackground(vararg params: Void?): String {
            try {
                url = URL("http://10.0.2.2:8000/api/offres/$offre")

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
            AsyncLoadCandidature().execute()

            try {
                var j = JSONObject(result)
                type.setText("Type de contrat : "+(j.optString("type").takeIf { it != "null" } ?: ""))
                titre.setText("Détails de votre candidature pour l'offre de "+(j.optString("titre").takeIf { it != "null" } ?: ""))
                entreprise.setText("Entreprise "+(j.optString("nom_entreprise").takeIf { it != "null" } ?: ""))
                var l = ""
                if ((j.optString("adresse_entreprise").takeIf { it != "null" } ?: "") !=""){
                    l = (j.optString("adresse_entreprise").takeIf { it != "null" } ?: "")
                    if ((j.optString("adresse_comp_entreprise").takeIf { it != "null" } ?: "") != ""){
                        l = l + " - " + (j.optString("adresse_comp_entreprise").takeIf { it != "null" } ?: "")
                        if ((j.optString("cp_entreprise").takeIf { it != "null" } ?: "") != "") {
                            l = l + ", "+ (j.optString("cp_entreprise").takeIf { it != "null" } ?: "")
                            if ((j.optString("ville_entreprise").takeIf { it != "null" } ?: "") != ""){
                                l = l + ", "+ (j.optString("ville_entreprise").takeIf { it != "null" } ?: "")
                                if ((j.optString("pays_entreprise").takeIf { it != "null" } ?: "") != ""){
                                    l = l + ", " + (j.optString("pays_entreprise").takeIf { it != "null" } ?: "")
                                }
                            } else {
                                if ((j.optString("pays_entreprise").takeIf { it != "null" } ?: "") != ""){
                                    l = l + ", "+ (j.optString("pays_entreprise").takeIf { it != "null" } ?: "")
                                }
                            }
                        } else {
                            if ((j.optString("ville_entreprise").takeIf { it != "null" } ?: "") != ""){
                                l = l + ", "+ (j.optString("ville_entreprise").takeIf { it != "null" } ?: "")
                                if ((j.optString("pays_entreprise").takeIf { it != "null" } ?: "") != ""){
                                    l = l + ", " + (j.optString("pays_entreprise").takeIf { it != "null" } ?: "")
                                }
                            } else {
                                if ((j.optString("pays_entreprise").takeIf { it != "null" } ?: "") != ""){
                                    l = l + ", "+ (j.optString("pays_entreprise").takeIf { it != "null" } ?: "")
                                }
                            }
                        }
                    } else {
                        if ((j.optString("cp_entreprise").takeIf { it != "null" } ?: "") != "") {
                            l = l + ", " + (j.optString("cp_entreprise").takeIf { it != "null" } ?: "")
                            if ((j.optString("ville_entreprise").takeIf { it != "null" } ?: "") != ""){
                                l = l + ", "+ (j.optString("ville_entreprise").takeIf { it != "null" } ?: "")
                                if ((j.optString("pays_entreprise").takeIf { it != "null" } ?: "") != ""){
                                    l = l + ", " + (j.optString("pays_entreprise").takeIf { it != "null" } ?: "")
                                }
                            } else {
                                if ((j.optString("pays_entreprise").takeIf { it != "null" } ?: "") != ""){
                                    l = l + ", "+ (j.optString("pays_entreprise").takeIf { it != "null" } ?: "")
                                }
                            }
                        } else {
                            if ((j.optString("ville_entreprise").takeIf { it != "null" } ?: "") != ""){
                                l = l + ", "+ (j.optString("ville_entreprise").takeIf { it != "null" } ?: "")
                                if ((j.optString("pays_entreprise").takeIf { it != "null" } ?: "") != ""){
                                    l = l + ", " + (j.optString("pays_entreprise").takeIf { it != "null" } ?: "")
                                }
                            } else {
                                if ((j.optString("pays_entreprise").takeIf { it != "null" } ?: "") != ""){
                                    l =l + ", "+ (j.optString("pays_entreprise").takeIf { it != "null" } ?: "")
                                }
                            }
                        }
                    }
                } else {
                    if ((j.optString("adresse_comp_entreprise").takeIf { it != "null" } ?: "") != ""){
                        l = (j.optString("adresse_comp_entreprise").takeIf { it != "null" } ?: "")
                        if ((j.optString("cp_entreprise").takeIf { it != "null" } ?: "") != "") {
                            l = l + ", "+ (j.optString("cp_entreprise").takeIf { it != "null" } ?: "")
                            if ((j.optString("ville_entreprise").takeIf { it != "null" } ?: "") != ""){
                                l = l + ", "+ (j.optString("ville_entreprise").takeIf { it != "null" } ?: "")
                                if ((j.optString("pays_entreprise").takeIf { it != "null" } ?: "") != ""){
                                    l = l + ", " + (j.optString("pays_entreprise").takeIf { it != "null" } ?: "")
                                }
                            } else {
                                if ((j.optString("pays_entreprise").takeIf { it != "null" } ?: "") != ""){
                                    l = l + ", "+ (j.optString("pays_entreprise").takeIf { it != "null" } ?: "")
                                }
                            }
                        } else {
                            if ((j.optString("ville_entreprise").takeIf { it != "null" } ?: "") != ""){
                                l = l + ", "+ (j.optString("ville_entreprise").takeIf { it != "null" } ?: "")
                                if ((j.optString("pays_entreprise").takeIf { it != "null" } ?: "") != ""){
                                    l = l + ", " + (j.optString("pays_entreprise").takeIf { it != "null" } ?: "")
                                }
                            } else {
                                if ((j.optString("pays_entreprise").takeIf { it != "null" } ?: "") != ""){
                                    l = l + ", "+ (j.optString("pays_entreprise").takeIf { it != "null" } ?: "")
                                }
                            }
                        }
                    } else {
                        if ((j.optString("cp_entreprise").takeIf { it != "null" } ?: "") != "") {
                            l = (j.optString("cp_entreprise").takeIf { it != "null" } ?: "")
                            if ((j.optString("ville_entreprise").takeIf { it != "null" } ?: "") != ""){
                                l = l + ", "+ (j.optString("ville_entreprise").takeIf { it != "null" } ?: "")
                                if ((j.optString("pays_entreprise").takeIf { it != "null" } ?: "") != ""){
                                    l = l + ", " + (j.optString("pays_entreprise").takeIf { it != "null" } ?: "")
                                }
                            } else {
                                if ((j.optString("pays_entreprise").takeIf { it != "null" } ?: "") != ""){
                                    l = l + ", "+ (j.optString("pays_entreprise").takeIf { it != "null" } ?: "")
                                }
                            }
                        } else {
                            if ((j.optString("ville_entreprise").takeIf { it != "null" } ?: "") != ""){
                                l = (j.optString("ville_entreprise").takeIf { it != "null" } ?: "")
                                if ((j.optString("pays_entreprise").takeIf { it != "null" } ?: "") != ""){
                                    l = l + ", " + (j.optString("pays_entreprise").takeIf { it != "null" } ?: "")
                                }
                            } else {
                                if ((j.optString("pays_entreprise").takeIf { it != "null" } ?: "") != ""){
                                    l = (j.optString("pays_entreprise").takeIf { it != "null" } ?: "")
                                }
                            }
                        }
                    }
                }
                localisation.setText("Adresse : "+l)
                periode.setText("Période : "+ (j.optString("periode").takeIf { it != "null" } ?: ""))
                date_publication.setText("Date de publication : "+(j.optString("date_publication").takeIf { it != "null" } ?: ""))
                if ((j.optString("salaire_min").takeIf { it != "null" } ?: "") != "" && (j.optString("salaire_max").takeIf { it != "null" } ?: "") != ""){
                    salaire.setText("Salaire : Entre "+j.getString("salaire_min")+" € et "+j.getString("salaire_max")+" € par an")
                } else if ((j.optString("salaire_min").takeIf { it != "null" } ?: "") == "" && (j.optString("salaire_max").takeIf { it != "null" } ?: "") != "") {
                    salaire.setText("Salaire : "+j.getString("salaire_max")+" €/an (Maximum)")
                } else if ((j.optString("salaire_min").takeIf { it != "null" } ?: "") != "" && (j.optString("salaire_max").takeIf { it != "null" } ?: "") == "") {
                    salaire.setText("Salaire : "+j.getString("salaire_min")+" €/an (Minimum)")
                }
                recruteur.setText("Nom du recruteur : "+(j.optString("prenom_recruteur").takeIf { it != "null" } ?: "")+" "+(j.optString("nom_recruteur").takeIf { it != "null" } ?: "").uppercase())
                details.setText((j.optString("description").takeIf { it != "null" } ?: ""))
            } catch (e: Exception) {

                e.printStackTrace()

                Toast.makeText(
                    this@InfoCandidatureActivity,
                    "Erreur de parsing JSON",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private inner class AsyncLoadCandidature : AsyncTask<Void, Void, String>() {
        private val pdLoading = ProgressDialog(this@InfoCandidatureActivity)
        private var co: HttpURLConnection? = null
        private var url: URL? = null

        override fun onPreExecute() {
            super.onPreExecute()

            pdLoading.setMessage("Chargement de la candidature...")
            pdLoading.setCancelable(false)
            pdLoading.show()
        }

        override fun doInBackground(vararg params: Void?): String {
            try {

                /*
                 * Récupération des candidatures
                 */

                url = URL("http://10.0.2.2:8000/api/candidature/$c")

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

            try {
                var j = JSONObject(result)
                statut.setText("Statut : "+(j.optString("statut").takeIf { it != "null" } ?: ""))

                val scoreString = j.optString("score_matching").takeIf { it != "null" } ?: ""
                if (scoreString.isNotEmpty()) {
                    score_m.setText("Score : " + j.getString("score_matching")+ " %")
                    score = scoreString.toDouble()
                    matcher.setText("Recalduler le score de matching")
                }
                date_candidature.setText("Date de candidature : "+(j.optString("date_candidature").takeIf { it != "null" } ?: ""))
                Log.i("score",score.toString())
            } catch (e: Exception) {
                e.printStackTrace()

                Toast.makeText(
                    this@InfoCandidatureActivity,
                    "Erreur de parsing JSON",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private inner class AsyncMatchScore : AsyncTask<Void, Void, String>() {
        private val pdLoading = ProgressDialog(this@InfoCandidatureActivity)
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
                score_m.setText("Score : " + j.getString("score")+ " %")
                score = j.getString("score").toDouble()
                matcher.setText("Recalduler le score de matching")
                Toast.makeText(
                    this@InfoCandidatureActivity,
                    "Score de matching calculé avec succès : "+j.getString("score")+" %",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                e.printStackTrace()

                Toast.makeText(
                    this@InfoCandidatureActivity,
                    "Erreur de parsing JSON",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}