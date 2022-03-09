package com.example.phoneverification

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.phoneverification.databinding.ActivityThirdBinding

class ThirdActivity : AppCompatActivity() {
    lateinit var binding: ActivityThirdBinding
    private var phoneNumber: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityThirdBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        phoneNumber = intent.getStringExtra("number")
        binding.apply {
            numberTv.text = phoneNumber
        }
    }
}