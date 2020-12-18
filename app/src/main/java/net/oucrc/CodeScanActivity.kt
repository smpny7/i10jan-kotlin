package net.oucrc

import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

@Suppress("DEPRECATION")
class CodeScanActivity : AppCompatActivity() {

    private val handler = Handler()
    private lateinit var soundPool: SoundPool
    private var sound = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_scan)

        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        soundPool = SoundPool.Builder()
            .setAudioAttributes(audioAttributes)
            .setMaxStreams(2)
            .build()

        sound = soundPool.load(this, R.raw.sound, 1)


        val integrator = IntentIntegrator(this).apply {
            captureActivity = CustomCodeScanActivity::class.java
        }

        integrator.setOrientationLocked(false)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("名札をかざしてください")
        integrator.setCameraId(1)

        integrator.setOrientationLocked(false)
        integrator.setBeepEnabled(false)
        integrator.setBarcodeImageEnabled(true)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        when {
            result != null -> {
                qrScannedAction(result.contents)
                Toast.makeText(this, "通信しています...", Toast.LENGTH_LONG).show()
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun qrScannedAction(member_key: String) {
        val url =
            "https://i10jan-api-test.herokuapp.com/v1.0/qrScannedAction?member_key=$member_key"
        val mimeType = MediaType.parse("application/json; charset=utf-8")
        val requestBody = RequestBody.create(mimeType, "{}")
        val request = Request.Builder().url(url).post(requestBody).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response?) {
                val body = response?.body()?.string()
                val jsonObject = JSONObject(body!!)

                // -----------------------------------------------------------------------------------------
                //      Return JSON
                // -----------------------------------------------------------------------------------------
                //    {
                //        "success": true,    -> Whether there were any errors in the server processing.
                //        "member": true,     -> Whether the user exists.
                //        "left": false       -> Whether the exit process has already been completed.
                //    }
                // -----------------------------------------------------------------------------------------

                val successFlag = jsonObject.getString("success").toString().toBoolean()

                if (!successFlag) {
                    handler.post {
                        Toast.makeText(applicationContext, "サーバでエラーが発生しました", Toast.LENGTH_LONG)
                            .show()
                    }
                    return
                }

                // If "success" is false, the "left" key does not exist.
                val leftFlag = jsonObject.getString("left").toString().toBoolean()

                if (leftFlag) {
                    handler.post {
                        soundPool.play(
                            sound,
                            1.0f,
                            1.0f,
                            1,
                            0,
                            1.0f
                        )
                        Toast.makeText(applicationContext, "退室しました", Toast.LENGTH_LONG)
                            .show()
                    }
                    return
                }

                val memberFlag = jsonObject.getString("member").toString().toBoolean()

                if (memberFlag) {
                    handler.post {
                        val intent = Intent(applicationContext, BodyTemperatureActivity::class.java)
                        intent.putExtra("memberKey", member_key)
                        startActivity(intent)
                    }
                } else {
                    handler.post {
                        Toast.makeText(applicationContext, "ユーザが存在しません", Toast.LENGTH_LONG)
                            .show()
                    }
                    return
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