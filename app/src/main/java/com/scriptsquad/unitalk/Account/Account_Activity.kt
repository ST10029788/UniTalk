package com.scriptsquad.unitalk.Account

import android.app.ProgressDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.scriptsquad.unitalk.R
import com.scriptsquad.unitalk.StartPage.Log_In_Screen
import com.scriptsquad.unitalk.Utilities.Utils
import com.scriptsquad.unitalk.databinding.ActivityAccountBinding

//method used from YouTube
//https://youtu.be/ymnKQVLs93c?si=vBY97O9HyEG277Pp
//channel: Philipp Lackner

class Account_Activity : AppCompatActivity() {
    private lateinit var binding:ActivityAccountBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog:ProgressDialog

    private companion object {
        private const val TAG = "ACCOUNT_ACTIVITY_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContentView(R.layout.activity_account)

        firebaseAuth=FirebaseAuth.getInstance()

        val sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE)
        val isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false)

        val languageSpinner: Spinner = findViewById(R.id.languageSpinner)

        val language = sharedPreferences.getString("selected_language", "en")
        changeLanguage(language ?: "en") // Default to English if no preference is set

        setContentView(R.layout.activity_account)

        // Create an ArrayAdapter using a string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.language_options, // Define this array in strings.xml
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            languageSpinner.adapter = adapter
        }

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedLanguage = parent.getItemAtPosition(position).toString()
                changeLanguage(selectedLanguage)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            binding.darkModeSwitch.isChecked = true
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor.putBoolean("isDarkModeOn", true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                editor.putBoolean("isDarkModeOn", false)
            }
            editor.apply()
            recreate()  // This line will restart the activity for the theme to take effect
        }

        progressDialog = ProgressDialog(this@Account_Activity)
        progressDialog.setTitle("please wait....")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.toolbarBackbtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        //method used from Google
        //https://firebase.google.com/docs/auth/web/password-auth
        //Firebase

        loadMyInfo()

        binding.logoutCv.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this@Account_Activity , Log_In_Screen::class.java))
            finish()

        }

        binding.editProfileCv.setOnClickListener {
            startActivity(Intent(this@Account_Activity, Edit_Profile_Activity::class.java))
        }

        binding.changePasswordCv.setOnClickListener {
            startActivity(Intent(this@Account_Activity, Change_Password_Activity::class.java))
        }
        binding.verifyAccountCv.setOnClickListener {
            verifyAccount()
        }
        binding.deleteAccountCv.setOnClickListener {
            startActivity(Intent(this@Account_Activity, Delete_Account_Activity::class.java))
        }


    }

    //method used from YouTube
    //https://youtu.be/GZnCHLEo6ng?si=gq68FKihAFGVrpPi
    //channel: Mohsen Mashkour



    private fun changeLanguage(language: String) {
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("selected_language", language)
        editor.apply()

        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)

        // Restart the activity to apply the language change
        val intent = intent
        finish()
        startActivity(intent)
    }

    private fun loadMyInfo() {

        //Retriving data from DataBase

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val dob = "${snapshot.child("dob").value}"
                    val email = "${snapshot.child("email").value}"
                    val phoneNumber = "${snapshot.child("phoneNumber").value}"
                    val phoneCode = "${snapshot.child("phoneCode").value}"
                    val profileImageUrl = "${snapshot.child("profileImageURl").value}"
                    var timestamp = "${snapshot.child("timestamp").value}"
                    val userType = "${snapshot.child("userType").value}"
                    val name = "${snapshot.child("name").value}"

                    val phone = phoneCode + phoneNumber

                    if (timestamp == "null") {
                        timestamp = "0"
                    }
                    //format time to dd/MM/yyyy
                    val formattedData = Utils.formatTimestampDate(timestamp.toLong())

                    //Set data to UI

                    binding.emailTv.text = email
                    binding.nameTv.text = name
                    binding.dobTv.text = dob
                    binding.phoneTv.text = phone
                    binding.memberSinceTv.text = formattedData

                    try {
                        Glide.with(this@Account_Activity)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.ic_person_black)
                            .into(binding.profileIvAccount)
                    } catch (e: Exception) {
                        Log.e(TAG, "onDataChanged", e)
                    }


                    if (userType == "Email") {
                        val isVerified = firebaseAuth.currentUser!!.isEmailVerified
                        if (isVerified) {
                            binding.verificationTv.text = getString(R.string.verified)
                            binding.verifyAccountCv.visibility = View.GONE
                        } else {
                            binding.verificationTv.text = getString(R.string.not_verified)
                            binding.verifyAccountCv.visibility = View.VISIBLE
                        }
                    } else {
                        binding.verificationTv.text = getString(R.string.verified)
                        binding.verifyAccountCv.visibility = View.GONE
                    }

                    // set Profile Image to ImageView




                }

                override fun onCancelled(error: DatabaseError) {
                  Log.e(TAG,"onDataChanged:$error")
                }


            })


    }
    //method used from YouTube
    //https://youtu.be/Wf7DDIaRYjk?si=aZNObTBh1DGau2k-
    //channel: SmallAcademy

    private fun verifyAccount() {
        Log.d(TAG, "verifying Account: ")

        try {
            progressDialog.setMessage("Sending verification link to email:")
            progressDialog.show()
            firebaseAuth.currentUser!!.sendEmailVerification().addOnSuccessListener {
                Log.d(TAG, "Account Verification link: sent successfully")
                progressDialog.dismiss()


            }.addOnFailureListener { e ->
                Log.e(TAG, "Failed to send verification link:", e)
                progressDialog.dismiss()
                Utils.toast(
                    this@Account_Activity,
                    "Failed to send verification link due to ${e.message}"
                )

            }
        } catch (e: Exception) {
            Utils.toast(this@Account_Activity, "${e.message}")

        }


    }
}