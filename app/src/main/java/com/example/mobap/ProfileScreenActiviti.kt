package com.example.mobap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import org.json.JSONObject

class ProfileScreenActiviti : AppCompatActivity() {
    lateinit var name: TextView
    lateinit var eMail: TextView
    lateinit var app: MyApp
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_screen_activiti)
        app = applicationContext as MyApp
        name = findViewById(R.id.name)
        eMail = findViewById(R.id.eMail)

        HttpHelper.requestGET(
            "http://cinema.areas.su/user",
            mapOf(
                "token" to app.token
            )

        ){result, error, code ->
            //тут проверка на код, полученный с апи вместо проверки резальт на ноль
            if(result!=null){
                val json = JSONObject(result)
                runOnUiThread {
                    name.text = json.getString("name")
                    eMail.text = json.getString("username")


                }
            }
            else
                AlertDialog.Builder(this)
                    .setTitle("Ошибка http-запроса")
                    .setMessage(error)
                    .setPositiveButton("OK", null)
                    .create()
                    .show()
        }
    }

    fun chatWindow(view: android.view.View) {
        startActivity(Intent(this, ChatActivity:: class.java))
    }
}