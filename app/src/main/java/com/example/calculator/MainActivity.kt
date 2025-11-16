package com.example.calculator

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_calculator).setOnClickListener {
            val intent = Intent(this, CalculatorActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_player).setOnClickListener {
            val intent = Intent(this, MediaPlayerActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_location).setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_telephony).setOnClickListener {
            // позже
        }

        findViewById<Button>(R.id.btn_sockets).setOnClickListener {
            // позже
        }

        findViewById<Button>(R.id.btn_views).setOnClickListener {
            // позже
        }
    }
}
