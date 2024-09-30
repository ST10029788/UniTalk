package com.scriptsquad.unitalk.ChatRoom.activities

import android.Manifest
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.scriptsquad.unitalk.ChatRoom.AdapterChatRoom
import com.scriptsquad.unitalk.ChatRoom.FirebaseCords.FirebaseCords
import com.scriptsquad.unitalk.ChatRoom.model.ChatRoomModel
import com.scriptsquad.unitalk.StartPage.Main_Home_Screen
import com.scriptsquad.unitalk.R
import com.scriptsquad.unitalk.databinding.ActivityChatRoomBinding
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import java.text.SimpleDateFormat
import java.util.Date


class Chat_Room_Activity : AppCompatActivity() {

    private lateinit var binding: ActivityChatRoomBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var chatRoomAdapter: AdapterChatRoom

    private lateinit var progressDialog: ProgressDialog

    private var message = ""



    private companion object {
        private const val TAG = "CHAT_ROOM_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityChatRoomBinding.inflate(layoutInflater)


        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setCanceledOnTouchOutside(false)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        showsplash()

        initChatList()

        chatRoomAdapter.startListening()

        binding.sendFab.setOnClickListener {
            addMessage()
        }

        binding.attachFab.setOnClickListener {
            openExplorer()
        }

        binding.imageBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

//method used from YouTube
//https://youtu.be/jgtxeilPLnI?si=wyZ4HSvI9m_9XIyP
//channel: Alex Mamo

    private fun initChatList() {

        val firebaseCords = FirebaseCords()

        binding.chatRoomRv.setHasFixedSize(true)


        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        layoutManager.stackFromEnd = false
        binding.chatRoomRv.layoutManager = layoutManager


        val query: Query =
            firebaseCords.MAIN_CHAT_DATABASE.orderBy("timestamp", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<ChatRoomModel>()
            .setQuery(query, ChatRoomModel::class.java)
            .build()



        chatRoomAdapter = AdapterChatRoom(options, this) {
            scrollToLastItem()
        }
        binding.chatRoomRv.adapter = chatRoomAdapter
        chatRoomAdapter.startListening()

    }
//method used from YouTube
//https://youtu.be/0gLr-pBIPhI?si=EfHlUd8kPy94o4FR
//PedroTech

    private fun addMessage() {
        val date = Date()
        val simpleDataFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        message = binding.chatBox.text.toString()
        val messageId = simpleDataFormat.format(date)

        if (!TextUtils.isEmpty(message)) {
            val uid = firebaseAuth.currentUser!!.uid
            val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child("${firebaseAuth.uid}")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val name = "${snapshot.child("name").value}"
                        val profileImageUrl = "${snapshot.child("profileImageURl").value}"
                        Log.d(TAG, "onDataChange: name:$name")
                        Log.d(TAG, "onDataChange: profileImageUrl: $profileImageUrl")
                        Log.d(TAG, "onDataChange: firebaseUid: $firebaseAuth")

                        val hashMap = HashMap<String, Any>()
                        hashMap["message"] = message
                        hashMap["userName"] = name
                        hashMap["timestamp"] = FieldValue.serverTimestamp()
                        hashMap["messageId"] = messageId
                        hashMap["profileImageUrl"] = profileImageUrl
                        hashMap["chat_image"] = ""
                        hashMap["chatTime"] = System.currentTimeMillis()
                        hashMap["uid"] = "${firebaseAuth.uid}"

                        val fireBaseCords = FirebaseCords()
                        fireBaseCords.MAIN_CHAT_DATABASE.document(messageId).set(hashMap)
                            .addOnSuccessListener {
                                Log.d(TAG, "addMessage:Success: $it")
                                Log.d(
                                    TAG,
                                    "onDataChange: timestamp: ${FieldValue.serverTimestamp()}"
                                )
                                Log.d(TAG, "onDataChange: messageId: $messageId")
                                Toast.makeText(
                                    this@Chat_Room_Activity,
                                    "Message Sent",
                                    Toast.LENGTH_SHORT
                                ).show()
                                chatRoomAdapter.notifyDataSetChanged()
                                binding.chatBox.setText("")
                                scrollToLastItem()
                            }.addOnFailureListener { e ->
                                Toast.makeText(
                                    this@Chat_Room_Activity,
                                    "Failed To Send",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e(TAG, "addMessage: Failed due to ${e.message}")
                            }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d(TAG, "onCancelled: $error")
                    }
                })
        }
    }
//method used from YouTube
//https://youtu.be/vaKFSUmZ31A?si=-eab9NBtwC4rwwNG
//Programmer World

    private fun openExplorer() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            chooseImage()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 20
                )
            else {
                Toast.makeText(
                    this@Chat_Room_Activity,
                    "storage Permission Needed",
                    Toast.LENGTH_SHORT
                ).show()
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 20
                )
            }


        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == 20) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@Chat_Room_Activity, "Permission Granted", Toast.LENGTH_SHORT)
                    .show()
                chooseImage()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // Use the returned uri.
            val uriContent = result.uriContent
            // optional usage

            // Proceed with your logic using the uriContent or uriFilePath
            val intent = Intent(this, Image_Upload_Preview_Activity::class.java)
            intent.putExtra("imageUri", uriContent.toString())
            startActivity(intent)

        } else {
            // Handle the error condition
            val exception = result.error
            Toast.makeText(this, exception?.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun chooseImage() {
        // Launch the CropImage activity with the specified options
        cropImage.launch(
            CropImageContractOptions(null, CropImageOptions().apply {
                guidelines = CropImageView.Guidelines.ON
            })
        )
    }


    private fun showsplash() {
        val dialog =
            Dialog(this, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.splash_chatroom)
        dialog.setCancelable(true)
        dialog.show()
        val handler = Handler()
        val runnable = Runnable {
            if (firebaseAuth.currentUser != null) {
                dialog.dismiss()
            } else if (firebaseAuth.currentUser == null) {
                startActivity(Intent(this, Main_Home_Screen::class.java))

            }

        }
        handler.postDelayed(runnable, 5000)
    }


    private fun scrollToLastItem() {
        binding.chatRoomRv.postDelayed({
            val itemCount = chatRoomAdapter.itemCount
            if (itemCount > 0) {
                binding.chatRoomRv.scrollToPosition(0)
            }
        }, 100)
    }


}