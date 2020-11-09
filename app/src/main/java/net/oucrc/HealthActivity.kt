package net.oucrc

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException


class HealthActivity : AppCompatActivity() {
    val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health)

        val nickname = intent.getStringExtra("nickname").toString()
        val memberKey = intent.getStringExtra("memberKey").toString()
        val bodyTemperature = intent.getStringExtra("bodyTemperature").toString()

        val physicalCondition: Spinner = findViewById(R.id.physical_condition)
        val stifling: Spinner = findViewById(R.id.stifling)
        val fatigue: Spinner = findViewById(R.id.fatigue)
        val remarks: EditText = findViewById(R.id.remarks)
        val nextButton: Button = findViewById(R.id.nextButton)

        nextButton.setOnClickListener {
            nextButton.isClickable = false
            val physicalConditionSelected = physicalCondition.selectedItem.toString()
            val stiflingSelected = stifling.selectedItem.toString()
            val fatigueSelected = fatigue.selectedItem.toString()
            val escapedRemarks = remarks.text.toString()
            registData(
                memberKey,
                bodyTemperature,
                physicalConditionSelected,
                stiflingSelected,
                fatigueSelected,
                escapedRemarks
            )
            nextButton.isClickable = true
        }


        val physicalConditionSpinner = findViewById<Spinner>(R.id.physical_condition)
        val physicalConditionSpinnerItems = arrayOf(
            "良好",
            "不調"
        )
        val physicalConditionAdapter = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            physicalConditionSpinnerItems
        )
        physicalConditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        physicalConditionSpinner.adapter = physicalConditionAdapter


        val stiflingSpinner = findViewById<Spinner>(R.id.stifling)
        val stiflingSpinnerItems = arrayOf(
            "なし",
            "あり"
        )
        val stiflingAdapter = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            stiflingSpinnerItems
        )
        stiflingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        stiflingSpinner.adapter = stiflingAdapter


        val fatigueSpinner = findViewById<Spinner>(R.id.fatigue)
        val fatigueSpinnerItems = arrayOf(
            "なし",
            "あり"
        )
        val fatigueAdapter = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            fatigueSpinnerItems
        )
        fatigueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fatigueSpinner.adapter = fatigueAdapter
    }

    fun registData(
        member_key: String,
        body_temperature: String,
        physical_condition: String,
        stifling: String,
        fatigue: String,
        remarks: String
    ) {
        val url =
            "https://script.google.com/macros/s/AKfycbx6yZFhNkbSVhWOoTULLEonM6u2UIVjh0x4g53HJw/exec?func=insertNewData&member_key=$member_key&body_temperature=$body_temperature&physical_condition=$physical_condition&stifling=$stifling&fatigue=$fatigue&remarks=$remarks"
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
                        Toast.makeText(applicationContext, "失敗しました", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(applicationContext, "登録しました", Toast.LENGTH_LONG).show()
                        val intent = Intent(applicationContext, MainActivity::class.java)
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