package com.example.donat_demo2.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.donat_demo2.R
import com.example.donat_demo2.model.UserModel
import com.example.donat_demo2.ui.auth.LoginActivity
import com.example.donat_demo2.utils.SharedPrefUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCCustomerInfoInitializer
import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCProductInitializer
import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCommerzInitialization
import com.sslwireless.sslcommerzlibrary.model.response.SSLCTransactionInfoModel
import com.sslwireless.sslcommerzlibrary.model.util.SSLCCurrencyType
import com.sslwireless.sslcommerzlibrary.model.util.SSLCSdkType
import com.sslwireless.sslcommerzlibrary.view.singleton.IntegrateSSLCommerz
import com.sslwireless.sslcommerzlibrary.viewmodel.listener.SSLCTransactionResponseListener
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "mainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var phoneNumber: String
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPrefUtils: SharedPrefUtils
    private lateinit var userList: ArrayList<UserModel>
    private var amount: Double? = null
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayShowHomeEnabled(true)

        auth = FirebaseAuth.getInstance()
        sharedPrefUtils = SharedPrefUtils(this)
        phoneNumber = sharedPrefUtils.getUserNumber().userNumber!!
        Log.d(TAG, "onCreate: $phoneNumber")
        userList = ArrayList()

        database()

        btn_donat.setOnClickListener {
            donate()
        }

    }

    private fun database() {
        firebaseDatabase = Firebase.database
        databaseReference = firebaseDatabase.getReference("users")
        id_user_list_count.text = "Loading..."
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userList.clear()
                if (dataSnapshot.exists()) {
                    for (snapshot in dataSnapshot.children) {
                        val userModel = snapshot.getValue(UserModel::class.java)
                        userList.add(userModel!!)
                        amount = userModel.amount!!.toDouble()
                        id_amount.text = amount.toString()
                    }
                }
                Log.d(TAG, "onDataChange: Total users: ${userList.size}")
                id_user_list_count.text = "Total users: ${userList.size} person"
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "onCancelled: ${p0.message}")
                Toast.makeText(this@MainActivity, "${p0.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun donate() {
        val donatAmount = id_et.text.toString()
        if (donatAmount.isEmpty()) {
            Toast.makeText(this, "Please enter amount to donat", Toast.LENGTH_SHORT).show()
        } else {

            val sslCommerzInitialization = SSLCommerzInitialization(
                "googl611cf29fa3b46",
                "googl611cf29fa3b46@ssl",
                donatAmount.toDouble(),
                SSLCCurrencyType.BDT,
                "123456789098765",
                "yourProductType",
                SSLCSdkType.TESTBOX
            )


            val sslCCustomerInfoInitializer = SSLCCustomerInfoInitializer(
                "CustomerName",
                "customerEmail",
                "customerAddress",
                "",
                "1341",
                "Bangladesh",
                "1234567890",
            )

            val sslCProductInitializer = SSLCProductInitializer(
                "productName",
                "productCategory",
                SSLCProductInitializer.ProductProfile.TravelVertical(
                    "productProfile",
                    "hoursTillDeparture",
                    "flightType",
                    "pnr",
                    "journeyFromTo"
                )
            )

            val sslcTransactionResponseListener = object : SSLCTransactionResponseListener {
                override fun transactionSuccess(p0: SSLCTransactionInfoModel?) {

                    val amm =  id_amount.text.toString()
                    val lastAmount = amm.toDouble()-donatAmount.toDouble()
                    saveUserInDatabase(lastAmount.toString())
                    id_et.text = null
                    Log.d(TAG, "transactionSuccess: $lastAmount")
                    Toast.makeText(this@MainActivity, p0.toString(), Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "transactionSuccess: ${p0.toString()}")
                }

                override fun transactionFail(p0: String?) {
                    Toast.makeText(this@MainActivity, p0.toString(), Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "transactionSuccess: ${p0.toString()}")
                }

                override fun merchantValidationError(p0: String?) {
                    Toast.makeText(this@MainActivity, p0.toString(), Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "transactionSuccess: ${p0.toString()}")
                }

            }

            IntegrateSSLCommerz
                .getInstance(this)
                .addSSLCommerzInitialization(sslCommerzInitialization)
                .addCustomerInfoInitializer(sslCCustomerInfoInitializer)
                .addProductInitializer(sslCProductInitializer)
                .buildApiCall(sslcTransactionResponseListener)
        }
    }

    fun saveUserInDatabase(balance:String) {
        val firebaseDatabase = Firebase.database
        val databaseReference = firebaseDatabase.getReference("users").child(phoneNumber).child("amount")
            databaseReference.setValue(balance).addOnCompleteListener { task->
                if (task.isSuccessful){
                    Log.d(TAG, "saveUserInDatabase: Data added again")
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_log_out -> {
                sharedPrefUtils.logOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finishAffinity()
            }
        }
        return super.onOptionsItemSelected(item)
    }


}