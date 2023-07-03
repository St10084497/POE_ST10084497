package com.example.studytime

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.util.*
import java.util.Timer

class splash : AppCompatActivity() {
    private   lateinit var imggif: ImageView
    var delay: Long = 5000
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        imggif = findViewById(R.id.imageView3)
        //load gif
        Glide.with(this).load(R.drawable.loading).into(imggif)

        val runsplash = Timer()
        val ShowSplash: TimerTask = object : TimerTask() {
            override fun run() {
                finish()
                val intent = Intent(this@splash, MainActivity::class.java)
                startActivity(intent)
            }
        }
        runsplash.schedule(ShowSplash, delay)
    }
}