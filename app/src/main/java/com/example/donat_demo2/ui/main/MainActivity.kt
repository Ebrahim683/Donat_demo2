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
import com.example.donatdemo1.utils.SharedPrefUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "mainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var phoneNumber: String
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPrefUtils: SharedPrefUtils
    private lateinit var userList: ArrayList<UserModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayShowHomeEnabled(true)

        auth = FirebaseAuth.getInstance()
        sharedPrefUtils = SharedPrefUtils(this)
        phoneNumber = sharedPrefUtils.getUserNumber().userNumber!!
        Log.d(TAG, "onCreate: $phoneNumber")
        userList = ArrayList()

        id_amount.text = "Balance = 500000 Taka"

        val firebaseDatabase = Firebase.database
        val databaseReference = firebaseDatabase.getReference("users")
        id_user_list_count.text = "Loading..."
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userList.clear()
                if (dataSnapshot.exists()) {
                    for (snapshot in dataSnapshot.children) {
                        val userModel = snapshot.getValue(UserModel::class.java)
                        userList.add(userModel!!)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var menu = menuInflater.inflate(R.menu.top_menu_item, menu)
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