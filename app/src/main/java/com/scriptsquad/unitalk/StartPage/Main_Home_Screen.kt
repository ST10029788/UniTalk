package com.scriptsquad.unitalk.StartPage

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.scriptsquad.unitalk.ChatRoom.activities.Chat_Room_Activity
import com.scriptsquad.unitalk.Notices_Page.activities.Notices_Activity
import com.scriptsquad.unitalk.Notices_Page.activities.Admin_Notices_Activity
import com.scriptsquad.unitalk.About_Page.About_Us_Activity
import com.scriptsquad.unitalk.Notes.activities.BooksAdminDashboardActivity
import com.scriptsquad.unitalk.Notes.activities.BooksDashboardUserActivity
import com.scriptsquad.unitalk.databinding.ActivityMainHomeBinding
import com.scriptsquad.unitalk.Socials_Page.activities.Socials_Gallery_Activity
import com.scriptsquad.unitalk.YTLectures_Page.Lectures_Activity
import com.scriptsquad.unitalk.Materials.activities.AdminPaperActivity
import com.scriptsquad.unitalk.Materials.activities.PaperUserActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.scriptsquad.unitalk.Account.Account_Activity
import com.scriptsquad.unitalk.Ai_Page.AI_Activity
import com.scriptsquad.unitalk.R

class Main_Home_Screen : AppCompatActivity() {
    private lateinit var binding: ActivityMainHomeBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private companion object {
        private const val TAG = "MAIN_HOME_ACTIVITY_TAG"
    }

    private var userMode = ""

    //method used from YouTube
    // https://youtu.be/H_maapn4Q3Q?si=_1siEM622Nqtcr-s
    //channel: TECH_WORLD
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainHomeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        showsplash()
        getUserName()
        subscribeToTopic()

        if (firebaseAuth.currentUser == null) {
            startActivity(Intent(this@Main_Home_Screen, Log_In_Screen::class.java))
        } else {
            updateFcmToken()
            askNotificationPermission()
        }

        binding.books.setOnClickListener {
            checkUserMode()
        }
        binding.chatRoomCv.setOnClickListener {
            when (userMode) {
                "USER" -> {
                    startActivity(Intent(this, Chat_Room_Activity::class.java))
                }

                "Guest" -> {
                    Toast.makeText(this@Main_Home_Screen, "Account Restricted", Toast.LENGTH_SHORT)
                        .show()
                }

                "Admin" -> {
                    startActivity(Intent(this@Main_Home_Screen, Chat_Room_Activity::class.java))
                }

                else -> {
                    Toast.makeText(this@Main_Home_Screen, "User Not Recognized", Toast.LENGTH_SHORT)
                        .show()

                }
            }

        }

        binding.personHomeIv.setOnClickListener {
            when (userMode) {
                "USER" -> {
                    startActivity(Intent(this@Main_Home_Screen, Account_Activity::class.java))
                }

                "Guest" -> {
                    Toast.makeText(this@Main_Home_Screen, "Account Restricted", Toast.LENGTH_SHORT)
                        .show()
                }

                "Admin" -> {
                    startActivity(Intent(this@Main_Home_Screen, Account_Activity::class.java))
                }

                else -> {
                    Toast.makeText(this@Main_Home_Screen, "User Not Recognized", Toast.LENGTH_SHORT)
                        .show()
                }

            }

        }

        binding.buyCv.setOnClickListener {
            when (userMode) {
                "USER" -> {
                    startActivity(Intent(this@Main_Home_Screen, Main_Activity::class.java))
                }

                "Guest" -> {
                    Toast.makeText(this@Main_Home_Screen, "Account Restricted", Toast.LENGTH_SHORT)
                        .show()
                }

                "Admin" -> {
                    startActivity(Intent(this@Main_Home_Screen, Main_Activity::class.java))
                }

                else -> {
                    Toast.makeText(this@Main_Home_Screen, "User Not Recognized", Toast.LENGTH_SHORT)
                        .show()
                }
            }


        }

        binding.LecturesCv.setOnClickListener {
            startActivity(Intent(this@Main_Home_Screen, Lectures_Activity::class.java))
        }

        binding.noticeCv.setOnClickListener {

            when (userMode) {
                "USER" -> {
                    startActivity(Intent(this, Notices_Activity::class.java))
                }

                "Guest" -> {
                    Toast.makeText(this@Main_Home_Screen, "Account Restricted", Toast.LENGTH_SHORT)
                        .show()
                }

                "Admin" -> {
                    startActivity(Intent(this@Main_Home_Screen, Admin_Notices_Activity::class.java))
                }

                else -> {
                    Toast.makeText(this@Main_Home_Screen, "User Not Recognized", Toast.LENGTH_SHORT)
                        .show()
                }
            }


        }

        binding.aiCv.setOnClickListener{
            startActivity(Intent(this@Main_Home_Screen,AI_Activity::class.java))
        }


        binding.accountCv.setOnClickListener {
            when (userMode) {
                "USER" -> {
                    startActivity(Intent(this@Main_Home_Screen, Account_Activity::class.java))
                }

                "Guest" -> {
                    Toast.makeText(this@Main_Home_Screen, "Account Restricted", Toast.LENGTH_SHORT)
                        .show()
                }

                "Admin" -> {
                    startActivity(Intent(this@Main_Home_Screen, Account_Activity::class.java))
                }

                else -> {
                    Toast.makeText(this@Main_Home_Screen, "User Not Recognized", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }

        binding.logoutCv.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this@Main_Home_Screen, Log_In_Screen::class.java))
            finish()
        }
        binding.aboutUsCv.setOnClickListener {
            startActivity(Intent(this@Main_Home_Screen, About_Us_Activity::class.java))

        }
        binding.galleryCv.setOnClickListener {

            when (userMode) {
                "USER" -> {
                    startActivity(Intent(this@Main_Home_Screen, Socials_Gallery_Activity::class.java))
                }

                "Guest" -> {
                    Toast.makeText(this@Main_Home_Screen, "Account Restricted", Toast.LENGTH_SHORT)
                        .show()
                }

                "Admin" -> {
                    startActivity(Intent(this@Main_Home_Screen, Socials_Gallery_Activity::class.java))
                }

                else -> {
                    Toast.makeText(this@Main_Home_Screen, "User Not Recognized", Toast.LENGTH_SHORT)
                        .show()
                }
            }


        }
        binding.paperCv.setOnClickListener {
            when (userMode) {
                "USER" -> {
                    startActivity(Intent(this@Main_Home_Screen, PaperUserActivity::class.java))
                }

                "Admin" -> {
                    startActivity(Intent(this@Main_Home_Screen, AdminPaperActivity::class.java))
                }

                "Guest" -> {
                    startActivity(Intent(this@Main_Home_Screen, PaperUserActivity::class.java))
                }

                else -> {
                    Toast.makeText(this@Main_Home_Screen, "User Not Recognized", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }


    }


    private fun getUserName() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child("${firebaseAuth.uid}").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = "${snapshot.child("name").value}"
                val profileImageURl = "${snapshot.child("profileImageURl").value}"
                userMode = "${snapshot.child("userMode").value}"

                binding.nameTv.text = name

                try {
                    Glide.with(this@Main_Home_Screen)
                        .load(profileImageURl)
                        .placeholder(R.drawable.ic_person_white)
                        .into(binding.personHomeIv)
                } catch (e: Exception) {
                    Log.e(TAG, "onDataChanged :", e)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled: $error")

            }
        })
    }

    //method was from YouTube
    //https://youtu.be/_9uHFGXLUnk?si=c-lY_lE9ZFsDsiIK
    //channel: Ravecode Android
    private fun showsplash() {
        val dialog =
            Dialog(this@Main_Home_Screen, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.activity_splash_screan)
        dialog.setCancelable(true)
        dialog.show()
        val handler = Handler()
        val runnable = Runnable {
            if (firebaseAuth.currentUser != null) {
                dialog.dismiss()
            } else if (firebaseAuth.currentUser == null) {
                startActivity(Intent(this@Main_Home_Screen, Log_In_Screen::class.java))
                finish()

            }

        }
        handler.postDelayed(runnable, 5000)
    }

    private fun checkUserMode() {
        when (userMode) {
            "USER" -> {
                startActivity(Intent(this, BooksDashboardUserActivity::class.java))
            }

            "Guest" -> {
                startActivity(Intent(this, BooksDashboardUserActivity::class.java))
            }

            "Admin" -> {
                startActivity(
                    Intent(
                        this@Main_Home_Screen,
                        BooksAdminDashboardActivity::class.java
                    )
                )
            }

            else -> {
                Toast.makeText(this@Main_Home_Screen, "User Not Recognized", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun updateFcmToken() {
        val myUid = "${firebaseAuth.uid}"
        Log.d(TAG, "updateFcmToken: ")

        FirebaseMessaging.getInstance().token

            .addOnSuccessListener { fcmToken ->

                Log.d(TAG, "updateFcmToken: fcmToken $fcmToken")
                val hashMap = HashMap<String, Any>()
                hashMap["fcmToken"] = fcmToken

                val ref = FirebaseDatabase.getInstance().getReference("Users")
                ref.child(myUid)
                    .updateChildren(hashMap)
                    .addOnSuccessListener {
                        Log.d(TAG, "updateFcmToken: FCM Token update to db success")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "updateFcmToken: ", e)
                    }

            }
            .addOnFailureListener { e ->
                Log.e(TAG, "updateFcmToken: ", e)
            }

    }
    //method used from YouTube
    //https://youtu.be/vUf0cIRtV8A?si=gMEKH3nHgdLOzsbx
    //channel: Generic Apps
    private fun askNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_DENIED
            ) {
                requestNotificationPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }

        }

    }

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->

        Log.d(TAG, "requestNotificationPermission: isGranted: $isGranted")
    }

    private fun subscribeToTopic() {
// Get an instance of FirebaseMessaging
        val firebaseMessaging = Firebase.messaging

// Define the topic name
        val topic = "/topics/all"

// Subscribe the app to the topic
        firebaseMessaging.subscribeToTopic(topic)
            .addOnSuccessListener {
                Log.d(TAG, "Subscribed to topic: $topic")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to subscribe to topic: $topic", exception)
            }

    }


}









