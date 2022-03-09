package com.example.phoneverification

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.phoneverification.databinding.ActivitySecondBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class SecondActivity : AppCompatActivity() {
    lateinit var binding: ActivitySecondBinding
    lateinit var auth: FirebaseAuth
    private var TAG = "SecondActivity"
    private var storedVerificationId: String = ""
    private var phoneNumber: String = ""

    lateinit var storedVerificationId1: String
    lateinit var resendToken1: PhoneAuthProvider.ForceResendingToken

    lateinit var countdown_timer: CountDownTimer
    var time_in_milli_seconds = 0L
    var time: String = "1"
    var START_MILLI_SECONDS = 60000L
    var isRunning: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySecondBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //storedVerificationId = intent.getStringExtra("storedVerificationId").toString()
        //phoneNumber = intent.getStringExtra("number").toString()
        auth = FirebaseAuth.getInstance()
        val bundle = intent.extras
        if (bundle != null) {
            phoneNumber = bundle.getString("number").toString()
            storedVerificationId = bundle.getString("storedVerificationId").toString()
            Log.d(TAG, "verificationID: $storedVerificationId")
            Log.d(TAG, "phonenumber: $phoneNumber")
        }
        binding.apply {
            numberTv.text = phoneNumber.substring(0,phoneNumber.length-4)
            numberTv.text = numberTv.text.toString()+" -** -**"
            time_in_milli_seconds = time.toLong() * 60000L
            startTimer(time_in_milli_seconds)

            retry.setOnClickListener {

                time_in_milli_seconds = time.toLong() * 60000L
                startTimer(time_in_milli_seconds)
                resendCode(phoneNumber)
            }
        }

        binding.apply {
            verificationCode.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    verifyCode()
                    val view = currentFocus
                    if (view != null) {
                        val imm: InputMethodManager =
                            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                }
                true
            }
            verificationCode.addTextChangedListener {
                if (it.toString().length == 6) {
                    verifyCode()
                }
            }
        }
    }

    private fun startTimer(time_in_seconds: Long) {
        countdown_timer = object : CountDownTimer(time_in_seconds, 1000) {
            override fun onFinish() {
                binding.retry.isClickable = true
            }

            override fun onTick(p0: Long) {
                binding.retry.isClickable = false
                time_in_milli_seconds = p0
                updateTextUI()
            }
        }
        isRunning = true
        countdown_timer.start()

    }



    private fun updateTextUI() {
        val minute = (time_in_milli_seconds / 1000) / 60
        val seconds = (time_in_milli_seconds / 1000) % 60
        binding.timer.text = "$minute:$seconds"
    }

    private fun resendCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId1: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:$verificationId1")

            // Save verification ID and resending token so we can use them later
            storedVerificationId1 = verificationId1
            resendToken1 = token


        }
    }

    private fun verifyCode() {
        val code = binding.verificationCode.text.toString()
        if (code.length == 6) {
            val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
            signInWithPhoneAuthCredential(credential)
        }
    }



    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this@SecondActivity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    //val user = task.result?.user

                    val intent = Intent(this, ThirdActivity::class.java)
                    intent.putExtra("number", phoneNumber)
                    startActivity(intent)
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }
}