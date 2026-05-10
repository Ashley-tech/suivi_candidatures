package com.example.suivicandidatures

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class ConfirmDeleteAccountActivity : AppCompatActivity(),View.OnClickListener {
    lateinit var oui : Button
    lateinit var non : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_delete_account)
        oui = findViewById(R.id.delete_account_button)
        non = findViewById(R.id.no_delete_account_button)
        oui.setOnClickListener(this)
        non.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.getId()){
            R.id.delete_account_button -> {
                var a = AccountManagement(this)
                a.logout()
                finish()
            }
            R.id.no_delete_account_button -> {
                finish()
            }
            else -> {

            }
        }
    }
}