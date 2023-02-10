package com.bhm.sdk.demo.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.bhm.netcore.R
import leakcanary.LeakCanary

/**
 * @author Buhuiming
 * @description:
 * @date :2023/2/6 11:15
 */
class StartActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        LeakCanary.runCatching {  }
        val button = findViewById<View>(R.id.btnOpen) as Button
        button.setOnClickListener {
            startActivity(Intent(this@StartActivity, MainActivity::class.java))
        }
    }
}