package net.oucrc

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val integrator = IntentIntegrator(this)
        integrator.setOrientationLocked(false)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("名札をかざしてください")
        integrator.setCameraId(0) // Use a specific camera of the device

        integrator.setOrientationLocked(false)
        integrator.setBeepEnabled(false)
        integrator.setBarcodeImageEnabled(true)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        when {
            result != null -> {
                userExistCheck(result.contents)
                Toast.makeText(this, "通信しています...", Toast.LENGTH_LONG).show()
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun userExistCheck(member_key: String) {
        val url =
            "https://script.google.com/macros/s/AKfycbx6yZFhNkbSVhWOoTULLEonM6u2UIVjh0x4g53HJw/exec?func=getMember&member_key=$member_key"
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

                handler.post {
                    val jsonObject = jsonArray.getJSONObject(0)
                    getCanLeave(
                        jsonObject.getString("member_key"),
                        jsonObject.getString("nickname")
                    )
                }
            }

            override fun onFailure(call: Call?, e: IOException?) {
                handler.post {
                    Toast.makeText(applicationContext, "ネットワークに接続できません", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    fun getCanLeave(member_key: String, nickname: String) {
        val url =
            "https://script.google.com/macros/s/AKfycbx6yZFhNkbSVhWOoTULLEonM6u2UIVjh0x4g53HJw/exec?func=getCanLeave&member_key=$member_key"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response?) {
                val body = response?.body()?.string()

                if (body == "true") {
                    setOutTime(member_key)
                } else {
                    handler.post {
                        val intent = Intent(applicationContext, WelcomeActivity::class.java)
                        intent.putExtra("memberKey", member_key)
                        intent.putExtra("nickname", nickname)
                        startActivity(intent)
                    }
                }
            }

            override fun onFailure(call: Call?, e: IOException?) {
                handler.post {
                    Toast.makeText(applicationContext, "ネットワークに接続できません", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    fun setOutTime(member_key: String) {
        val url =
            "https://script.google.com/macros/s/AKfycbx6yZFhNkbSVhWOoTULLEonM6u2UIVjh0x4g53HJw/exec?func=setOutTime&member_key=$member_key"
        val MIMEType = MediaType.parse("application/json; charset=utf-8")
        val requestBody = RequestBody.create(MIMEType, "{}")
        val request = Request.Builder().url(url).post(requestBody).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response?) {
                val body = response?.body()?.string()
                val jsonObject = JSONObject(body)

                handler.post {
                    if (jsonObject.getString("success") == "false") {
                        Toast.makeText(applicationContext, "退出処理に失敗しました", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(applicationContext, "退出しました", Toast.LENGTH_LONG).show()
                    }
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