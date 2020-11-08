package net.oucrc

import android.os.Bundle
import android.os.Handler
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONArray
import java.io.IOException


class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val handler = Handler()

        val nicknameView: TextView = findViewById(R.id.nicknameView)
        val editBodyTemperature: EditText = findViewById(R.id.editBodyTemperature)

        editBodyTemperature.requestFocus()


//        val intent = intent
//        Toast.makeText(this, intent.getStringExtra("ReadData").toString(), Toast.LENGTH_LONG).show()

        val url =
            "https://script.google.com/macros/s/AKfycbx6yZFhNkbSVhWOoTULLEonM6u2UIVjh0x4g53HJw/exec?func=getMember&member_key=1"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response?) {
                val body = response?.body()?.string()
                val jsonArray = JSONArray(body)

                if (jsonArray.length() == 0) {
                    handler.post {
                        Toast.makeText(applicationContext, "ユーザーが存在しません", Toast.LENGTH_LONG).show()
                    }
                    return
                }

                val jsonObject = jsonArray.getJSONObject(0)
                println(jsonObject.getString("nickname"))
                handler.post {
                    nicknameView.text = jsonObject.getString("nickname")
                }
            }

            override fun onFailure(call: Call?, e: IOException?) {
                handler.post {
                    Toast.makeText(applicationContext, "ネットワークに接続できません", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}