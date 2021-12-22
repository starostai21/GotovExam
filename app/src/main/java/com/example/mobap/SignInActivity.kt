package com.example.mobap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import org.json.JSONObject

class SignInActivity : AppCompatActivity() {

    lateinit var eMailEditText: EditText
    lateinit var passwordEditText: EditText
    private lateinit var math:String
    private lateinit var app: MyApp
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        eMailEditText = findViewById(R.id.eMailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        app = applicationContext as MyApp
        val re = Regex("""[a-z0-9]+@[a-z0-9]\.[a-z]{1,3}$""")
        math = re.find(eMailEditText.text.toString()).toString()
    }

    fun signIn(view: android.view.View) {
        if (eMailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty() && math != null) {
            HttpHelper.requestPOST(
                "http://cinema.areas.su/auth/login",
                JSONObject().put("username", eMailEditText.text)
                    .put("password", passwordEditText.text),
                mapOf(
                    "Content-Type" to "application/json"
                )
            ) { result, error, code ->
                runOnUiThread {
                    if (code == 200) {
                        if (result != null) {
                            try {
                                val jsonResp = JSONObject(result)
                                app.token = jsonResp.getJSONObject("result").getString("token")
                                    runOnUiThread {
                                        startActivity(Intent(this, MainActivity::class.java))
                                        Toast.makeText(
                                            this,
                                            "Success get token: ${app.token}",
                                            Toast.LENGTH_LONG
                                        )
                                            .show()
                                    }

                            } catch (e: Exception) {
                                runOnUiThread {
                                    AlertDialog.Builder(this)
                                        .setTitle("Ошибка")
                                        .setMessage(e.message)
                                        .setPositiveButton("OK", null)
                                        .create()
                                        .show()
                                }
                            }
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

    fun registr(view: View) {
        startActivity(Intent(this, SignUpActivity:: class.java))
    }
}