package com.example.suivicandidatures

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class WelcomeActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var continuer: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        continuer = findViewById(R.id.button_continuer);
        continuer.setOnClickListener(this);
    }

    override fun onClick(p0: View?) {
        val account = AccountManagement(this)
        if (account.isAccountLogin()) {
            startActivity(Intent(this, MainMenuActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish();
    }


}