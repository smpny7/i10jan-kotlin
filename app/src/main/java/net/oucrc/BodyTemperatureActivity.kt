package net.oucrc

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BodyTemperatureActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_temperature)

        val bodyTemperatureEditor: EditText = findViewById(R.id.body_temperature_editor)
        val nextButton: Button = findViewById(R.id.jump_to_medical_consultation_button)

        val memberKey = intent.getStringExtra("memberKey").toString()

        bodyTemperatureEditor.requestFocus()
        bodyTemperatureEditor.filters = arrayOf<InputFilter>(
            DecimalDigitLimitFilter(2, 1)
        )

        bodyTemperatureEditor.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                nextButton.isEnabled = !(inputCheck(bodyTemperatureEditor.text.toString()))
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        bodyTemperatureEditor.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                nextButton.callOnClick()
                return@OnKeyListener true
            }
            false
        })

        nextButton.setOnClickListener {
            if (inputCheck(bodyTemperatureEditor.text.toString())) {
                Toast.makeText(this, "35.0以上38.0未満で入力してください", Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(applicationContext, MedicalConsultationActivity::class.java)
                intent.putExtra("memberKey", memberKey)
                intent.putExtra("bodyTemperature", bodyTemperatureEditor.text.toString())
                startActivity(intent)
            }
        }
    }

    private fun inputCheck(bodyTemperatureText: String): Boolean {
        return bodyTemperatureText.isEmpty()
                || bodyTemperatureText.toDoubleOrNull() == null
                || bodyTemperatureText.toDouble() < 35.0
                || bodyTemperatureText.toDouble() >= 38.0
    }
}