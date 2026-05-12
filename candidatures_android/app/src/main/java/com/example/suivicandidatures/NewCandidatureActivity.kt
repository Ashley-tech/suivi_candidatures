package com.example.suivicandidatures

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class NewCandidatureActivity : AppCompatActivity() , View.OnClickListener{
    lateinit var add : Button
    lateinit var retour : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_candidature)
        add = findViewById(R.id.add_candidature_button)
        retour = findViewById(R.id.new_to_candidature_button)
        add.setOnClickListener(this)
        retour.setOnClickListener(this)
    }
    override fun onClick(v: View){
        when (v.getId()){
            R.id.add_candidature_button -> {

            }
            R.id.new_to_candidature_button -> {
                finish()
            }
            else -> {

            }
        }
    }
}