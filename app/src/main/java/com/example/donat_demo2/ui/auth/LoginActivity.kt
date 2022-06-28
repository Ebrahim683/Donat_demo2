package com.example.donat_demo2.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.donat_demo2.ui.main.MainActivity
import com.example.donat_demo2.R
import com.example.donat_demo2.ui.otp.OTPActivity
import com.example.donat_demo2.utils.SharedPrefUtils
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import kotlinx.android.synthetic.main.activity_login.*

private const val TAG = "loginActivity"
class LoginActivity : AppCompatActivity() {

    lateinit var sharedPrefUtils: SharedPrefUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val countryCode: CountryCodePicker = findViewById(R.id.id_country_code);
        countryCode.registerPhoneNumberTextView(id_phone_number);

        sharedPrefUtils = SharedPrefUtils(this)
        if (sharedPrefUtils.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btn_id_send_otp.setOnClickListener {
            val intent = Intent(this, OTPActivity::class.java)
            intent.putExtra("ccp", countryCode.fullNumberWithPlus.trim())
            startActivity(intent)
        }


    }
}