package com.example.phoneverification

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.phoneverification.databinding.ActivityJustBinding

class JustActivity : AppCompatActivity() {
    var START_MILLI_SECONDS = 60000L
    lateinit var countdown_timer: CountDownTimer
//    var time_in_milli_seconds = 0L
//    var isRunning: Boolean = false;
    lateinit var binding: ActivityJustBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJustBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            button.setOnClickListener {
                if (isRunning) {
                    //pauseTimer()
                } else {
                    val time = binding.timeEditText.text.toString()
                    time_in_milli_seconds = time.toLong() * 60000L
                    startTimer(time_in_milli_seconds)
                }
            }

            reset.setOnClickListener {
                resetTimer()
            }

        }
    }

//    private fun pauseTimer() {
//
//        binding.button.text = "Start"
//        countdown_timer.cancel()
//        isRunning = false
//        binding.reset.visibility = View.VISIBLE
//    }

    private fun startTimer(time_in_seconds: Long) {
        countdown_timer = object : CountDownTimer(time_in_seconds, 1000) {
            override fun onFinish() {
                Toast.makeText(this@JustActivity, "good", Toast.LENGTH_SHORT).show()
            }

            override fun onTick(p0: Long) {
                time_in_milli_seconds = p0
                updateTextUI()
            }
        }
        countdown_timer.start()

       // isRunning = true
        //binding.button.text = "Pause"
        //binding.reset.visibility = View.INVISIBLE

    }

    private fun resetTimer() {
        time_in_milli_seconds = START_MILLI_SECONDS
        updateTextUI()
        binding.reset.visibility = View.INVISIBLE
    }

    private fun updateTextUI() {
        val minute = (time_in_milli_seconds / 1000) / 60
        val seconds = (time_in_milli_seconds / 1000) % 60

        binding.timer.text = "$minute:$seconds"
    }



}