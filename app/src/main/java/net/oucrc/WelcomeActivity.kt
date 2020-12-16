package net.oucrc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val editBodyTemperature: EditText = findViewById(R.id.editBodyTemperature)
        val nextButton: Button = findViewById(R.id.nextButton)

        val memberKey = intent.getStringExtra("memberKey").toString()

        editBodyTemperature.requestFocus()

        nextButton.setOnClickListener {
            nextButton.isClickable = false
            if (editBodyTemperature.text.toString().isNullOrEmpty()
                || editBodyTemperature.text.toString().toDoubleOrNull() == null
                || editBodyTemperature.text.toString().toDouble() < 35.0
                || editBodyTemperature.text.toString().toDouble() >= 38.0
            ) {
                Toast.makeText(this, "35.0以上38.0未満で入力してください", Toast.LENGTH_LONG).show()
                nextButton.isClickable = true
            } else {
                val intent = Intent(applicationContext, HealthActivity::class.java)
                intent.putExtra("memberKey", memberKey)
                intent.putExtra("bodyTemperature", editBodyTemperature.text.toString())
                startActivity(intent)
            }
        }
    }
}