package com.example.mobap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import org.json.JSONObject

class SignUpActivity : AppCompatActivity() {
    lateinit var eMail: EditText
    lateinit var password: EditText
    lateinit var name: EditText
    lateinit var surName: EditText
    lateinit var app: MyApp
    lateinit var math: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        app = applicationContext as MyApp
        eMail = findViewById(R.id.eMail)
        password = findViewById(R.id.password)
        name = findViewById(R.id.name)
        surName = findViewById(R.id.surName)
        val re = Regex("""[a-z0-9]+@[a-z0-9]\.[a-z]{1,3}$""")
        math = re.find(eMail.text.toString()).toString()
    }

    fun haveAcount(view: android.view.View) {
        startActivity(Intent(this, SignInActivity:: class.java))
    }

    fun logIn(view: android.view.View) {
        if (eMail.text.isNotEmpty() && password.text.isNotEmpty() && math != null && name.text.isNotEmpty() && surName.text.isNotEmpty()) {
            HttpHelper.requestPOST(
                "http://cinema.areas.su/auth/register",
                JSONObject().put("username", eMail.text)
                    .put("password", password.text)
                    .put("name", name.text)
                    .put("surName", surName.text),
                mapOf(
                    "Content-Type" to "application/json"
                )
            ) { result, error, code ->
                runOnUiThread {
                    if (code == 201) {
                        startActivity(Intent(this, SignInActivity::class.java))
                    } else
                        AlertDialog.Builder(this)
                            .setTitle("Ошибка http-запроса")
                            .setMessage(error)
                            .setPositiveButton("OK", null)
                            .create()
                            .show()
                }
            }
        }
        else{
            AlertDialog.Builder(this)
                .setTitle("Ошибка")
                .setMessage("Поля пустые, или заполненые не верно")
                .setPositiveButton("Cancel", null)
                .create()
                .show()
        }
    }

}