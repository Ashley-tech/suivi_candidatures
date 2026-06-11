package com.example.suivicandidatures

import android.annotation.SuppressLint
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.InputStream
import java.util.concurrent.Executors

class NewCVActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var retour : Button
    lateinit var select : Button
    lateinit var adding : Button
    lateinit var resultat : TextView
    lateinit var mess : TextView
    private lateinit var filePicker: ActivityResultLauncher<Array<String>>
    private var selectedFileUri: Uri? = null
    lateinit var extras : Bundle
    var id = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_cvactivity)
        mess = findViewById(R.id.message_cv)
        resultat = findViewById(R.id.result_cv)
        retour = findViewById(R.id.new_cv_to_cv_button)
        retour.setOnClickListener(this)
        select = findViewById(R.id.select_file_button)
        select.setOnClickListener(this)
        adding = findViewById(R.id.add_cv_button)
        adding.setOnClickListener(this)
        extras = intent.extras!!
        id = extras.getInt("id")
        filePicker = registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->
            if (uri != null) {
                selectedFileUri = uri
                val cursor = contentResolver.query(
                    uri,
                    null,
                    null,
                    null,
                    null
                )
                cursor?.use {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                    it.moveToFirst()
                    val fileName = it.getString(nameIndex)
                    val fileSize = it.getLong(sizeIndex)
                    Log.d("FILE_NAME", fileName)
                    Log.d("FILE_SIZE", fileSize.toString())
                    resultat.setText(fileName)
                    mess.setText("")
                }
                val type = contentResolver.getType(uri)
                Log.d("FILE_TYPE", type ?: "Type inconnu")
            }
        }
    }

    override fun onClick(v: View) {
        when (v.getId()){
            R.id.new_cv_to_cv_button -> {
                finish()
            }
            R.id.add_cv_button -> {
                mess.setText("")
                if (selectedFileUri == null) {
                    mess.setText("Veuillez sélectionner un fichier CV")
                    return
                }
                uploadCvOkHttp()
            }
            R.id.select_file_button -> {
                filePicker.launch(
                    arrayOf(
                        "application/pdf", // PDF
                        "application/msword", // DOC
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // DOCX
                        "application/vnd.oasis.opendocument.text" // ODT
                    )
                )
            }
            else -> {

            }
        }
    }

    private fun uploadCvOkHttp() {
        val uri = selectedFileUri ?: run {
            Toast.makeText(this, "Veuillez sélectionner un fichier", Toast.LENGTH_SHORT).show()
            return
        }

        val executor = Executors.newSingleThreadExecutor()

        executor.execute {
            try {
                val client = OkHttpClient()

                val fileName = getFileName(uri)
                //val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"
                val mimeType = contentResolver.getType(uri) ?: "application/json"

                // Convert URI -> temp file (IMPORTANT pour stabilité)
                val tempFile = createTempFileFromUri(uri)

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("compte", id.toString())
                    .addFormDataPart(
                        "cv",
                        fileName,
                        tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
                    )
                    .build()

                val request = Request.Builder()
                    .url("http://10.0.2.2:8000/api/cv/upload")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: "no response"

                runOnUiThread {
                    //Log.d("UPLOAD_RESULT", body)
                    if (!response.isSuccessful) {
                        Log.e("UPLOAD", response.body?.string() ?: "error")
                    }

                    if (response.isSuccessful) {
                        Toast.makeText(this, "Upload OK", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, "Erreur: ${response.code}", Toast.LENGTH_LONG).show()
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Exception: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun createTempFileFromUri(uri: Uri): File {
        val inputStream: InputStream = contentResolver.openInputStream(uri)!!
        val file = File(cacheDir, "upload_${System.currentTimeMillis()}")

        file.outputStream().use { output ->
            inputStream.copyTo(output)
        }

        inputStream.close()
        return file
    }

    private fun getFileName(uri: Uri): String {
        var name = "file"

        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst()) {
                name = it.getString(index)
            }
        }

        return name
    }
}