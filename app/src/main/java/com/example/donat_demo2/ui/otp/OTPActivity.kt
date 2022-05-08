package com.example.donat_demo2.ui.otp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.donat_demo2.R
import com.example.donat_demo2.model.UserModel
import com.example.donat_demo2.ui.main.MainActivity
import com.example.donatdemo1.utils.SharedPrefUtils
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_otpactivity.*
import java.util.concurrent.TimeUnit

private const val TAG = "oTPActivity"

class OTPActivity : AppCompatActivity() {

    private lateinit var phoneNumber: String
    private lateinit var auth: FirebaseAuth
    private lateinit var otpId: String
    private lateinit var authToken: String
    private lateinit var sharedPrefUtils: SharedPrefUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpactivity)

        sharedPrefUtils = SharedPrefUtils(this)

        phoneNumber = intent.getStringExtra("ccp")!!
        Log.d(TAG, "onCreate: $phoneNumber")
        id_number.text = "Phone : $phoneNumber"

        id_send_again.isClickable = false

        val counter = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                id_timer.text = "${millisUntilFinished / 1000} S"
            }

            override fun onFinish() {
                id_timer.text = "Try again if code is not send"
                id_send_again.isClickable = true
            }

        }
        counter.start()

        auth = FirebaseAuth.getInstance()
        sendOtp()

        btn_id_next.setOnClickListener {
            Log.d(TAG, "onCreate: ${id_otp_view.otp}")
            if (id_otp_view.otp?.isEmpty() == true) {
                Toast.makeText(this@OTPActivity, "Invalid OTP", Toast.LENGTH_SHORT).show()
            } else {
                val credential = PhoneAuthProvider.getCredential(otpId, id_otp_view.otp.toString())
                signInWithPhoneAuthCredential(credential)
            }

        }

        id_send_again.setOnClickListener {
            sendOtp()
        }

    }


    fun sendOtp() {

        val callBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            //when sim is not in current device
            override fun onCodeSent(s: String, token: PhoneAuthProvider.ForceResendingToken) {
                otpId = s
                authToken = token.toString()
                Log.d(TAG, "onCodeSent: OTP id : $otpId, Token = $authToken")
            }

            //when sim is available in current device
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential)
                Log.d(TAG, "onVerificationCompleted: ${phoneAuthCredential.smsCode}")
            }

            override fun onVerificationFailed(firebaseException: FirebaseException) {
                Toast.makeText(this@OTPActivity, "$firebaseException", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "signInWithPhoneAuthCredential: $firebaseException")
            }

        }
        PhoneAuthProvider.getInstance()
            .verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, this, callBack)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    Log.d(TAG, "signInWithPhoneAuthCredential: ${credential.smsCode.toString()}")
                    startActivity(Intent(this, MainActivity::class.java))
                    sharedPrefUtils.saveUser(user?.uid.toString(),phoneNumber)
                    val userModel = UserModel(user!!.uid, phoneNumber)
                    saveUserInDatabase(userModel)
                    finishAffinity()
                } else {
                    Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "signInWithPhoneAuthCredential: ${task.exception}")
                }
            }
    }

    fun saveUserInDatabase(userModel: UserModel) {
        val firebaseDatabase = Firebase.database
        val databaseReference =
            firebaseDatabase.getReference("users").child(phoneNumber)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                databaseReference.setValue(userModel)

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

}