package com.scriptsquad.unitalk.Marketplace.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.scriptsquad.unitalk.R
import com.scriptsquad.unitalk.Utilities.Utils
import com.scriptsquad.unitalk.Marketplace.adapter.AdapterChat
import com.scriptsquad.unitalk.Marketplace.model.ModelChat
import com.scriptsquad.unitalk.databinding.ActivityChatBinding
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.lang.Exception


class Chat_Activity : AppCompatActivity() {

    private var receiptUid = ""
    private var receiptFcmToken = ""

    // Will generate using UIDs of user and receipt
    private var chatPath = ""

    //Uri of the image picked from Camera/ Gallery
    private var imageUri: Uri? = null

    private var myUid = ""
    private var myName = ""

    private lateinit var adapterChat: AdapterChat

    private lateinit var binding: ActivityChatBinding


    private lateinit var progressDialog: ProgressDialog

    private lateinit var firebaseAuth: FirebaseAuth

    private companion object {
        private const val TAG = "CHAT_TAG"
    }

//method used from YouTube
//https://youtu.be/q6TL2RyysV4?si=2z5opxFHHiR9O1CR
//Philipp Lackner

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityChatBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //get the uid of the receipt(as we passed in Chat Activity class while starting this Activity)
        receiptUid = intent.getStringExtra("receiptUid")!!

        myUid = firebaseAuth.uid!!

        chatPath = Utils.chatPath(receiptUid, myUid)


        loadMyInfo()
        loadReceiptDetails()
        loadMessage()


        binding.toolbarBackbtn.setOnClickListener {
            finish()
        }

        binding.attachFab.setOnClickListener {
            imagePickDialog()
        }

        binding.sendFab.setOnClickListener {
            validateData()
        }


    }


    private fun loadMyInfo() {
        Log.d(TAG, "loadMyInfo: ")
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                myName = "${snapshot.child("name").value}"
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
//method used from Digital Ocean
//https://www.digitalocean.com/community/tutorials/android-capture-image-camera-gallery
//author: Anupam Chugh

    private fun imagePickDialog() {
        Log.d(TAG, "imagePickDialog: ")

        val popMenu = PopupMenu(this@Chat_Activity, binding.attachFab)
        popMenu.menu.add(Menu.NONE, 1, 1, "Camera")
        popMenu.menu.add(Menu.NONE, 2, 2, "Gallery")

        popMenu.show()

        popMenu.setOnMenuItemClickListener { menuItem ->
            val itemId = menuItem.itemId
            if (itemId == 1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestCameraPermissions.launch(arrayOf(android.Manifest.permission.CAMERA))
                } else {
                    requestCameraPermissions.launch(
                        arrayOf(
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    )
                }

            } else if (itemId == 2) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pickImageGallery()
                } else {
                    requestStoragePermission.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
            true
        }

    }

    private val requestCameraPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        Log.d(TAG, "requestCameraPermissions: ")

        var areAllGranted = true
        for (isGranted in result.values) {
            areAllGranted = areAllGranted && isGranted
        }

        if (areAllGranted) {
            Log.d(TAG, "requestCameraPermissions: All permissions e.g. Camera and Storage granted ")
            pickImageCamera()
        } else {
            Log.d(
                TAG,
                "requestCameraPermissions:All permissions or some of Camera and Storage denied "
            )
            Utils.toast(this@Chat_Activity, "All permissions or some of Camera and Storage denied")
        }


    }

    private val requestStoragePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d(TAG, "requestStoragePermission: $isGranted")

        if (isGranted) {
            pickImageGallery()
        } else {
            Utils.toast(this@Chat_Activity, "Permission denied...")
        }
    }
//method used from YouTube
//https://youtu.be/KwyuOgJq1Tw?si=ft2t-R7suwZZ377q
//channel: The Android Factory

    private fun loadReceiptDetails() {
        Log.d(TAG, "loadReceiptDetails: ")

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(receiptUid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val name = "${snapshot.child("name").value}"
                        val profileImageUrl = "${snapshot.child("profileImageURl").value}"
                        receiptFcmToken = "${snapshot.child("fcmToken").value}"

                        Log.d(TAG, "onDataChange: name: $name")
                        Log.d(TAG, "onDataChange: profileImageUrl: $profileImageUrl")
                        Log.d(TAG, "onDataChange: receiptFcmToken: $receiptFcmToken")

                        binding.toolbarTitleTv.text = name

                        try {
                            Glide.with(this@Chat_Activity)
                                .load(profileImageUrl)
                                .placeholder(R.drawable.ic_person_white)
                                .into(binding.toolbarProfileIv)
                        } catch (e: Exception) {
                            Log.e(TAG, "onDataChanged", e)
                        }


                    } catch (e: Exception) {
                        Log.e(TAG, "onDataChange: ", e)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
//method used from Digital Ocean
//https://www.digitalocean.com/community/tutorials/android-capture-image-camera-gallery
//author: Anupam Chugh

    private fun loadMessage() {

        Log.d(TAG, "loadMessage: ")

        val messageArrayList = ArrayList<ModelChat>()

        val ref = FirebaseDatabase.getInstance().getReference("Chats")
        ref.child(chatPath)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageArrayList.clear()

                    for (ds: DataSnapshot in snapshot.children) {

                        try {

                            val modelChat = ds.getValue(ModelChat::class.java)

                            messageArrayList.add(modelChat!!)
                        } catch (e: Exception) {
                            Log.e(TAG, "onDataChange: ", e)
                        }


                        adapterChat = AdapterChat(this@Chat_Activity, messageArrayList)
                        binding.chatRv.adapter = adapterChat


                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

    }


    private fun pickImageCamera() {
        Log.d(TAG, "pickImageCamera: ")

        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "THE_IMAGE_TITLE")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "THE_IMAGE_DESCRIPTION")

        imageUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)


        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }

    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "cameraActivityResultLauncher: imageUri: $imageUri")
            uploadToFirebaseStorage()
        } else {
            Utils.toast(this@Chat_Activity, "Canceled...")
        }
    }
//method used from YouTube
//https://youtu.be/nOtlFl1aUCw?si=dFodeHMYiNqKVzgH
//channel: Coding Test

    private fun pickImageGallery() {
        Log.d(TAG, "pickImageGallery: ")

        val intent = Intent(Intent.ACTION_PICK)

        intent.type = "image/*"
        galleryActivityForResult.launch(intent)
    }

    private val galleryActivityForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == Activity.RESULT_OK) {

            val data = result.data

            imageUri = data!!.data
            Log.d(TAG, "galleryActivityForResult: imageUri: $imageUri")
            uploadToFirebaseStorage()

        } else {
            Utils.toast(this@Chat_Activity, "Cancelled...!")
        }
    }
//method used from YouTube
//https://youtu.be/DWIGAkYkpg8?si=aYJgqh8hQdGRbcra
//Android Knowledge

    private fun uploadToFirebaseStorage() {
        Log.d(TAG, "uploadToFirebaseStorage: ")

        // show progress
        progressDialog.setMessage("Uploading image...")
        progressDialog.show()

        val timestamp = Utils.getTimestamp()

        val filePathAndName = "ChatImages/$timestamp"

        val storageRef = FirebaseStorage.getInstance().getReference(filePathAndName)

        //compress chatImages before uploading it to database Storage
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream)
        val reducedImage: ByteArray = byteArrayOutputStream.toByteArray()

        storageRef.putBytes(reducedImage)
            .addOnProgressListener { snapshot ->

                val progress = 100.0 * snapshot.bytesTransferred / snapshot.totalByteCount

                progressDialog.setMessage("Uploading image: Progress ${progress.toUInt()} %")

            }.addOnSuccessListener { taskSnapshot ->

                val uriTask = taskSnapshot.storage.downloadUrl

                while (!uriTask.isSuccessful);
                val uploadImageUrl = uriTask.result.toString()

                if (uriTask.isSuccessful) {
                    sendMessage(Utils.MESSAGE_TYPE_IMAGE, uploadImageUrl, timestamp)
                }

            }.addOnFailureListener { e ->

                progressDialog.dismiss()
                Log.e(TAG, "uploadToFirebaseStorage: ", e)
                Utils.toast(this@Chat_Activity, "Failed to Upload due to ${e.message}")
            }

    }

    private fun validateData() {
        Log.d(TAG, "validateData: ")
        //input data

        val message = binding.messageEt.text.toString().trim()
        val timestamp = Utils.getTimestamp()

        if (message.isEmpty()) {
            Utils.toast(this@Chat_Activity, "Message cannot Empty...")
        } else {
            sendMessage(Utils.MESSAGE_TYPE_TEXT, message, timestamp)
        }

    }
//method used from Android.com
//https://developer.android.com/develop/ui/views/notifications/build-notification
//Studio Developers

    private fun sendMessage(messageType: String, message: String, timestamp: Long) {
        Log.d(TAG, "sendMessage: messsageType: $messageType")
        Log.d(TAG, "sendMessage: message: $message")
        Log.d(TAG, "sendMessage: timestamp: $timestamp")

        //show progress
        progressDialog.setMessage("Sending message...!")
        progressDialog.show()

        val refChat = FirebaseDatabase.getInstance().getReference("Chats")

        val keyId = "${refChat.push().key}"

        val hashMap = HashMap<String, Any>()
        hashMap["messageId"] = "$keyId"
        hashMap["messageType"] = "$messageType"
        hashMap["message"] = "$message"
        hashMap["fromUid"] = "$myUid"
        hashMap["toUid"] = "$receiptUid"
        hashMap["timestamp"] = timestamp

        refChat.child(chatPath)
            .child(keyId)
            .setValue(hashMap)

            .addOnSuccessListener {
                Log.d(TAG, "sendMessage: message sent")
                progressDialog.dismiss()

                binding.messageEt.setText("")

                // if message type is text show actual message as Notification/description. if message type is image pass "Sent an attachment"
                if (messageType == Utils.MESSAGE_TYPE_TEXT) {
                    prepareNotification(message)
                } else {
                    prepareNotification("Sent an attachment")
                }


            }

            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Log.e(TAG, "sendMessage: ", e)
                Utils.toast(this@Chat_Activity, "Failed to send due to ${e.message}")
            }

    }

    private fun prepareNotification(message: String) {

        Log.d(TAG, "prepareNotification: ")

        val notificationJo = JSONObject()
        val notificationDataJo = JSONObject()
        val notificationNotificationJo = JSONObject()

        try {

            notificationDataJo.put("notificationType", "${Utils.NOTIFICATION_TYPE_NEW_MESSAGE}")
            notificationDataJo.put("senderUid", "${firebaseAuth.uid}")

            notificationNotificationJo.put("title", "$myName")
            notificationNotificationJo.put("body", "$message")
            notificationNotificationJo.put("sound", "default")


            notificationJo.put("to", "$receiptFcmToken")
            notificationJo.put("notification", notificationNotificationJo)
            notificationJo.put("data", notificationDataJo)

        } catch (e: Exception) {
            Log.e(TAG, "prepareNotification: ", e)
        }

        sendFcmNotification(notificationJo)


    }
//method used from Google
//https://firebase.google.com/docs/cloud-messaging
//Firebase

    private fun sendFcmNotification(notificationJo: JSONObject) {

        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            "https://fcm.googleapis.com/fcm/send",
            notificationJo,
            Response.Listener {
                // Notification sent
                Log.d(TAG, "sendFcmNotification: Notification Send $it")

            },
            Response.ErrorListener { e ->

                //Notification failed to sent
                Log.e(TAG, "sendFcmNotification: ", e)

            }

        ) {

            override fun getHeaders(): MutableMap<String, String> {

                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/Json"
                headers["Authorization"] = "key=${Utils.FCM_SERVER_KEY}"

                return headers

            }

        }

        Volley.newRequestQueue(this).add(jsonObjectRequest)

    }

}