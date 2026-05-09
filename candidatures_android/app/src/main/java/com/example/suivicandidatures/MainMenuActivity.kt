package com.example.suivicandidatures

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainMenuActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var candidatures : Button
    lateinit var profile : Button
    lateinit var deconnect : Button
    lateinit var title : TextView
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
    }

    override fun onClick(v: View) {
        when (v.getId()){
            R.id.menu_to_profil_button -> {

            }
            R.id.deconnect_button -> {

            }
            R.id.menu_to_candidatures_button -> {

            }
            else -> {

            }
        }
    }
}