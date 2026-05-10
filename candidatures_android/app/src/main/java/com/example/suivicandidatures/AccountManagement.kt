package com.example.suivicandidatures
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

class AccountManagement(private val co: Context) {

    private var sP: SharedPreferences =
        co.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    var editor: SharedPreferences.Editor = sP.edit()

    companion object {
        const val PREF_NAME = "User_login"
        const val LOGINB = "is_user_login"
        const val LOGIN = "login"
    }

    fun isAccountLogin(): Boolean {
        return sP.getBoolean(LOGINB, false) && sP.getString(LOGIN, null) != null
    }

    fun getLogin(): String? {
        return sP.getString(LOGIN, null)
    }

    fun accountSessionManage(
        login: String
    ) {
        editor.putBoolean(LOGINB, true)
        editor.putString(LOGIN, login)
        editor.apply()

        val intent = Intent(co, MainMenuActivity::class.java)
        intent.putExtra("login", login)

        co.startActivity(intent)

        (co as LoginActivity).finish()
    }

    fun accountSessionUpdate(
        login: String
    ) {
        editor.putString(LOGIN, login)
        editor.apply()
    }

    fun checkLogin() {
        if (!isAccountLogin()) {
            val intent = Intent(co, LoginActivity::class.java)
            co.startActivity(intent)
            if (co is MainMenuActivity) {
                co.finish()
            }
        }
    }

    fun userDetails(): HashMap<String, String?> {
        val account = HashMap<String, String?>()
        account[LOGIN] = sP.getString(LOGIN, null)
        return account
    }

    fun updateInfo(
        login: String
    ) {
        editor.clear().apply()
        //editor.commit()

        editor.putBoolean(LOGINB, true)
        editor.putString(LOGIN, login)
        editor.apply()
    }

    fun logout() {
        editor.clear()
        editor.apply()
        val intent = Intent(co, LoginActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        co.startActivity(intent)
    }

    fun deleteAccount() {
        editor.clear().apply()
        val intent = Intent(co, LoginActivity::class.java)
        co.startActivity(intent)
        (co as ConfirmDeleteAccountActivity).finish()
    }
}