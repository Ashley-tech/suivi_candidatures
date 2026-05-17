package com.example.suivicandidatures

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView

import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class CandidaturesActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var retour: Button
    lateinit var nouv : Button
    lateinit var extras : Bundle
    lateinit var candidatures : ListView
    var id = 0
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_candidatures)
        retour = findViewById(R.id.candidatures_to_menu_button)
        retour.setOnClickListener(this)
        nouv = findViewById(R.id.candidatures_to_new_button)
        nouv.setOnClickListener(this)
        candidatures = findViewById(R.id.candidatures_list)
        extras = intent.extras!!
        id = extras.getInt("id")
        Log.i("id",id.toString())
        AsyncLoadCandidatures().execute()
    }
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {

            Log.i("REFRESH", "Rechargement des candidatures")

            AsyncLoadCandidatures().execute()
        }
    }

    override fun onClick(v: View){
        when (v.getId()){
            R.id.candidatures_to_new_button -> {
                startActivityForResult(Intent(this,NewCandidatureActivity::class.java),1)
            }
            R.id.candidatures_to_menu_button -> {
                finish()
            }
            else -> {

            }
        }
    }

    private inner class AsyncLoadCandidatures : AsyncTask<Void, Void, String>() {

        private val pdLoading = ProgressDialog(this@CandidaturesActivity)

        private var co: HttpURLConnection? = null
        private var url: URL? = null

        override fun onPreExecute() {
            super.onPreExecute()

            pdLoading.setMessage("Chargement des candidatures...")
            pdLoading.setCancelable(false)
            pdLoading.show()
        }

        override fun doInBackground(vararg params: Void?): String {
            try {

                /*
                 * Récupération des candidatures
                 */

                url = URL("http://10.0.2.2:8000/api/compte/$id/candidatures")

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

            Log.d("CANDIDATURES_RESULT", result)

            try {

                val jsonArray = JSONArray(result)

                val list = ArrayList<HashMap<String, String>>()

                for (i in 0 until jsonArray.length()) {

                    val candidature = jsonArray.getJSONObject(i)

                    val map = HashMap<String, String>()

                    map["id"] = candidature.getInt("id").toString()
                    map["offre"] = candidature.getInt("offre").toString()

                    map["date"] =
                        candidature.optString("date_candidature")
                            .takeIf { it != "null" } ?: ""

                    map["statut"] =
                        candidature.optString("statut")
                            .takeIf { it != "null" } ?: ""

                    list.add(map)
                }

                val adapter = CandidatureAdapter(
                    this@CandidaturesActivity,
                    list
                )

                candidatures.adapter = adapter

            } catch (e: Exception) {

                e.printStackTrace()

                Toast.makeText(
                    this@CandidaturesActivity,
                    "Erreur de parsing JSON",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    class CandidatureAdapter(
        private val context: Context,
        private val list: ArrayList<HashMap<String, String>>
    ) : BaseAdapter() {

        override fun getCount(): Int = list.size

        override fun getItem(position: Int): Any = list[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(
            position: Int,
            convertView: View?,
            parent: ViewGroup?
        ): View {
            val view = LayoutInflater.from(context).inflate(R.layout.item_candidature, parent, false)

            val item01 = view.findViewById<TextView>(R.id.itemcandidatures01)
            val item02 = view.findViewById<TextView>(R.id.itemcandidatures02)
            val item03 = view.findViewById<TextView>(R.id.itemcandidatures03)

            val infoButton = view.findViewById<Button>(R.id.candidatures_to_info_button)

            val deleteButton = view.findViewById<Button>(R.id.delete_candidatures_button)

            val item = list[position]

            val idCandidature = item["id"].toString()
            val idOffre = item["offre"].toString()

            item01.text = "Offre ID : $idOffre"
            item02.text = item["date"]
            item03.text = item["statut"]

            /*
             * Voir l'offre
             */

            infoButton.setOnClickListener {
                val intent = Intent(context,InfoCandidatureActivity::class.java)
                intent.putExtra("id_candidature",idCandidature.toInt())
                intent.putExtra("id_offre",idOffre.toInt())
                (context as AppCompatActivity).startActivityForResult(intent, 1)
            }

            /*
             * Supprimer
             */

            deleteButton.setOnClickListener {

                val intent = Intent(
                    context,
                    ConfirmDeleteCandidatureActivity::class.java
                )

                intent.putExtra(
                    "id_candidature",
                    idCandidature.toInt()
                )
                intent.putExtra("id_offre",idOffre.toInt())
                intent.putExtra("previous_activity", "CandidaturesActivity")

                (context as AppCompatActivity).startActivityForResult(intent, 1)
            }

            return view
        }
    }
}