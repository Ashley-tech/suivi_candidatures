package com.example.suivicandidatures

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class ConfirmLogoutActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var oui : Button
    lateinit var non : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_logout)
        oui = findViewById(R.id.logout_button)
        oui.setOnClickListener(this);
        non = findViewById(R.id.no_logout_button)
        non.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.getId()){
            R.id.logout_button -> {
                var a = AccountManagement(this)
                a.logout()
                finish()
            }
            R.id.no_logout_button -> {
                finish()
            }
            else -> {

            }
        }
    }
}