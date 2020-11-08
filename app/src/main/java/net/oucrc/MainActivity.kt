package net.oucrc

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
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
//                Toast.makeText(this, result.contents, Toast.LENGTH_LONG).show()

                val intent = Intent(this, WelcomeActivity::class.java)
                intent.putExtra("ReadData", result.contents)
                startActivity(intent)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }
}