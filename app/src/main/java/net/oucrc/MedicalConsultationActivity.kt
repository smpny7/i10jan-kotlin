package net.oucrc

import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

@Suppress("DEPRECATION")
class MedicalConsultationActivity : AppCompatActivity() {

    private val handler = Handler()
    private lateinit var soundPool: SoundPool
    private var sound = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medical_consultation)

        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        soundPool = SoundPool.Builder()
            .setAudioAttributes(audioAttributes)
            .setMaxStreams(2)
            .build()

        sound = soundPool.load(this, R.raw.sound, 1)


        val memberKey = intent.getStringExtra("memberKey").toString()
        val bodyTemperature = intent.getStringExtra("bodyTemperature").toString()

        val checkbox1: CheckBox = findViewById(R.id.medical_consultation_checkbox_1)
        val checkbox2: CheckBox = findViewById(R.id.medical_consultation_checkbox_2)
        val checkbox3: CheckBox = findViewById(R.id.medical_consultation_checkbox_3)
        val registrationButton: Button = findViewById(R.id.registration_button)

        checkbox1.setOnClickListener {
            registrationButton.isEnabled =
                checkbox1.isChecked && checkbox2.isChecked && checkbox3.isChecked
        }

        checkbox2.setOnClickListener {
            registrationButton.isEnabled =
                checkbox1.isChecked && checkbox2.isChecked && checkbox3.isChecked
        }

        checkbox3.setOnClickListener {
            registrationButton.isEnabled =
                checkbox1.isChecked && checkbox2.isChecked && checkbox3.isChecked
        }

        registrationButton.setOnClickListener {
            registrationButton.isEnabled = false
            registration(memberKey, bodyTemperature)
        }
    }

    private fun registration(member_key: String, body_temperature: String) {
        val url =
            "https://i10jan-api-test.herokuapp.com/v1.0/registerAction?member_key=$member_key&body_temperature=$body_temperature&physical_condition=良好&stifling=なし&fatigue=なし"
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

                handler.post {
                    if (!successFlag) {
                        Toast.makeText(applicationContext, "サーバでエラーが発生しました", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        soundPool.play(
                            sound,
                            1.0f,
                            1.0f,
                            1,
                            0,
                            1.0f
                        )
                        Toast.makeText(applicationContext, "登録しました", Toast.LENGTH_LONG).show()
                        val intent = Intent(applicationContext, CodeScanActivity::class.java)
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
}