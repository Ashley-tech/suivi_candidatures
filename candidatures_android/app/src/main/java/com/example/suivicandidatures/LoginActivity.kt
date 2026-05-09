package com.example.suivicandidatures

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.apache.http.params.CoreConnectionPNames.CONNECTION_TIMEOUT
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
import org.json.JSONObject;

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var inscription : Button;
    lateinit var display : Button;
    lateinit var login : Button;
    lateinit var forgot : Button;
    lateinit var mdp : EditText;
    lateinit var email : EditText;
    lateinit var resultat : TextView;
    var passwordVisible = false;
    public val READ_TIMEOUT : Int = 15000
    public val CONNECTION_TIMEOUT : Int = 15000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        inscription = findViewById(R.id.signupBtn);
        inscription.setOnClickListener(this);
        display = findViewById(R.id.buttondisplay);
        display.setOnClickListener(this);
        login = findViewById(R.id.loginBtn);
        login.setOnClickListener(this);
        forgot = findViewById(R.id.oublieBtn);
        forgot.setOnClickListener(this);
        mdp = findViewById(R.id.pwd_input);
        email = findViewById(R.id.login_input);
        resultat = findViewById(R.id.result_login)
    }

    fun regexCheck(re: Regex, str: String):Boolean{
        return re.matches(str)
    }
    override fun onClick(p0: View) {
        var i : Intent;
        when (p0.getId()){
            R.id.signupBtn -> {
                i = Intent(this,SignupActivity::class.java);
                startActivity(i);
            }
            R.id.buttondisplay -> {
                if (passwordVisible) {
                    mdp.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    passwordVisible = false;
                } else {
                    mdp.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    passwordVisible = true;
                }
            }
            R.id.oublieBtn -> {
                i = Intent(this,ForgotActivity::class.java)
                startActivity(i)
            }
            R.id.loginBtn -> {
                if (mdp.text.toString() == "" || email.text.toString() == ""){
                    resultat.text = "Tous les champs sont obligatoires !"
                } else {
                    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
                    AsyncConnect().execute(
                        email.text.toString(),
                        mdp.text.toString()
                    )
                }
            }
            else -> {

            }
        }
    }

    private inner class AsyncConnect : AsyncTask<String, Void, String>() {
        private val pdLoading = ProgressDialog(this@LoginActivity)
        private var co: HttpURLConnection? = null
        private var url: URL? = null
        override fun onPreExecute() {
            super.onPreExecute()

            pdLoading.setMessage("Vérification de vos informations...")
            pdLoading.setCancelable(false)
            pdLoading.show()
        }

        override fun doInBackground(vararg strings: String?): String {
            try {
                url = URL("http://10.0.2.2:8000/api/login")
            } catch (e: MalformedURLException) {
                e.printStackTrace()
                return "false"
            }

            try {
                co = url!!.openConnection() as HttpURLConnection
                co!!.readTimeout = READ_TIMEOUT
                co!!.connectTimeout = CONNECTION_TIMEOUT
                co!!.requestMethod = "POST"
                co!!.setRequestProperty(
                    "Content-Type",
                    "application/x-www-form-urlencoded"
                )

                co!!.doInput = true
                co!!.doOutput = true

                Log.i("a", "a")

                val builder = Uri.Builder()
                    .appendQueryParameter("email", strings[0])
                    .appendQueryParameter("mdp", strings[1])

                val query = builder.build().encodedQuery

                Log.i("a", "b")

                val os: OutputStream = co!!.outputStream

                Log.i("a", "c")

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
                val message = json.getString("message")
                if (!success) {
                    resultat.text = "Email et/ou mot de passe incorrect(s)"
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Connexion réussie",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    "Erreur de parsing JSON",
                    Toast.LENGTH_LONG
                ).show()
                resultat.text = "Une erreur s'est produite"
            }
        }
    }


}