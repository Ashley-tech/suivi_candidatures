package com.example.suivicandidatures

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var inscription : Button;
    lateinit var display : Button;
    lateinit var login : Button;
    lateinit var forgot : Button;
    lateinit var mdp : EditText;
    lateinit var email : EditText;
    var passwordVisible = false;
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

            }
            R.id.loginBtn -> {
                if (mdp.text.toString() == "" || email.text.toString() == ""){
                    Toast.makeText(this,"Tous les champs sont obligatoires !",Toast.LENGTH_SHORT).show()
                } else {
                    
                }
            }
            else -> {

            }
        }
    }


}