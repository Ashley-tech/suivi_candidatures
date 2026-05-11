package com.example.suivicandidatures

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
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
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

class CVsActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var retour : Button
    lateinit var new_cv : Button
    lateinit var cvs : ListView
    lateinit var extras : Bundle
    var id = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cvs)
        retour = findViewById(R.id.cv_to_profile_button)
        new_cv = findViewById(R.id.cv_to_new_button)
        retour.setOnClickListener(this)
        new_cv.setOnClickListener(this)
        cvs = findViewById(R.id.cvs_list)
        extras = intent.extras!!
        id = extras.getInt("id")
        AsyncLoadCVs().execute()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {

            AsyncLoadCVs().execute()
        }
    }

    override fun onClick(v: View) {
        when (v.getId()){
            R.id.cv_to_profile_button -> {
                finish()
            }
            R.id.cv_to_new_button -> {
                startActivityForResult(Intent(this, NewCVActivity::class.java).putExtra("id",id), 1)
            }
            else -> {

            }
        }
    }

    private inner class AsyncLoadCVs : AsyncTask<Void, Void, String>() {

        private val pdLoading = ProgressDialog(this@CVsActivity)

        private var co: HttpURLConnection? = null
        private var url: URL? = null

        override fun onPreExecute() {
            super.onPreExecute()

            pdLoading.setMessage("Chargement des CVs...")
            pdLoading.setCancelable(false)
            pdLoading.show()
        }

        override fun doInBackground(vararg params: Void?): String {
            try {
                url = URL("http://10.0.2.2:8000/api/compte/$id/cvs")
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
            } catch (e: Exception) {
                e.printStackTrace()
                return "Exception"
            }

            return try {
                val responseCode = co!!.responseCode
                Log.d("HTTP_CODE", responseCode.toString())
                if (responseCode == HttpURLConnection.HTTP_OK) {
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
            } catch (e: Exception) {
                e.printStackTrace()
                "Exception"
            } finally {
                co?.disconnect()
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            pdLoading.dismiss()

            if (result == null) return

            Log.d("CV_RESULT", result)
            try {
                val jsonArray = JSONArray(result)
                val list = ArrayList<HashMap<String, String>>()
                for (i in 0 until jsonArray.length()) {
                    val cv = jsonArray.getJSONObject(i)
                    val map = HashMap<String, String>()
                    map["id"] = cv.getInt("id").toString()
                    map["nom"] = cv.optString("nom").takeIf { it != "null" } ?: ""
                    map["date"] = cv.optString("date_upload").takeIf { it != "null" } ?: ""
                    list.add(map)
                }

                val adapter = CVAdapter(
                    this@CVsActivity,
                    list
                )

                cvs.adapter = adapter
            } catch (e: Exception) {
                e.printStackTrace()

                Toast.makeText(
                    this@CVsActivity,
                    "Erreur de parsing JSON",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    class CVAdapter(
        private val context: Context,
        private val list: ArrayList<HashMap<String, String>>
    ) : BaseAdapter() {
        override fun getCount(): Int = list.size

        override fun getItem(position: Int): Any = list[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.item_cv, parent, false)

            val itemcv01 = view.findViewById<TextView>(R.id.itemcv01)
            val itemcv02 = view.findViewById<TextView>(R.id.itemcv02)

            val downloadButton = view.findViewById<Button>(R.id.download_cv_button)
            val deleteButton = view.findViewById<Button>(R.id.delete_cv_button)

            val item = list[position]

            val idCV = item["id"].toString()

            itemcv01.text = item["nom"]
            itemcv02.text = item["date"]

            /*
             * Télécharger
             */

            downloadButton.setOnClickListener {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://10.0.2.2:8000/api/cv/$idCV/download")
                )
                context.startActivity(intent)
            }

            /*
             * Supprimer
             */

            deleteButton.setOnClickListener {
                val intent = Intent(
                    context,
                    ConfirmDeleteCVActivity::class.java
                )
                intent.putExtra("id_cv", idCV.toInt())
                intent.putExtra("nom_cv",itemcv01.text)
                (context as AppCompatActivity).startActivityForResult(intent, 1)
            }
            return view
        }
    }
}