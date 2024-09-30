package com.scriptsquad.unitalk.StartPage

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.scriptsquad.unitalk.Utilities.Utils
import com.scriptsquad.unitalk.databinding.ActivityRegisterEmailBinding
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

//method used from YouTube
//https://youtu.be/tbh9YaWPKKs?si=qh4THfrN5JV7ZaHL
//SmallAcademy

class Register_Using_Email_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterEmailBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private companion object {
        private const val TAG = "REGISTER_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegisterEmailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.ToolbarBackbtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.haveAccountBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.RegisterBtn.setOnClickListener {
            validData()
        }
    }

    // input data
    private var name = ""
    private var email = ""
    private var password = ""
    private var cPassword = ""

    private fun validData() {
        name = binding.nameEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        cPassword = binding.ConfirmpasswordEt.text.toString().trim()

        Log.d(TAG, "validateData: email :$email")
        Log.d(TAG, "validateData: password :$password")
        Log.d(TAG, "validateData: confirmPassword :$cPassword")

        val allowedDomains = listOf("@vcconnect.edu.za", "@iie.ac.za", "@varsitycollege.co.za")

        // Validate email domain
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEt.error = "Invalid Email Pattern"
            binding.emailEt.requestFocus()
        } else if (!allowedDomains.any { email.endsWith(it) }) {
            binding.emailEt.error = "Email domain not allowed. Use @vcconnect.edu.za, @iie.ac.za, or @varsitycollege.co.za"
            binding.emailEt.requestFocus()
        } else if (password.isEmpty()) {
            binding.passwordEt.error = "Enter Password"
            binding.passwordEt.requestFocus()
        } else if (cPassword.isEmpty()) {
            binding.ConfirmpasswordEt.error = "Enter Confirm Password"
            binding.ConfirmpasswordEt.requestFocus()
        } else if (password != cPassword) {
            binding.ConfirmpasswordEt.error = "Password Doesn't Match"
            binding.ConfirmpasswordEt.requestFocus()
        } else if (name.isEmpty()) {
            binding.nameEt.error = "Field Required"
            binding.nameEt.requestFocus()
        } else {
            registerUser()
        }
    }

    private fun registerUser() {
        progressDialog.setMessage("Creating Account")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                progressDialog.setMessage("Registered Successfully")
                MotionToast.createColorToast(
                    this,
                    "Upload Completed!",
                    "Registered Successfully",
                    MotionToastStyle.SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular)
                )
                updateUserInfo()
                progressDialog.dismiss()
                Log.d(TAG, "register user: Register Success")

            }.addOnFailureListener { e ->
                Log.e(TAG, "register user $e")
                progressDialog.dismiss()
                Utils.toast(this, "Failed to create Account due to ${e.message}")
            }
    }

    private fun updateUserInfo() {
        Log.d(TAG, "updateUserInfo")
        progressDialog.setMessage("Saving User Info")

        val timeStamp = Utils.getTimestamp()
        val registerUserEmail = firebaseAuth.currentUser!!.email
        val registeredUserUid = firebaseAuth.uid

        val hashMap = HashMap<String, Any>()
        hashMap["name"] = "$name"
        hashMap["phoneCode"] = ""
        hashMap["phoneNumber"] = ""
        hashMap["profileImageURl"] = ""
        hashMap["dob"] = ""
        hashMap["userType"] = "Email"
        hashMap["typingTo"] = ""
        hashMap["timestamp"] = timeStamp
        hashMap["onlineStatus"] = true
        hashMap["email"] = "$registerUserEmail"
        hashMap["uid"] = "$registeredUserUid"
        hashMap["userMode"] = "${Utils.USER_MODE}"

        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.child(registeredUserUid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "updateUserInfo: User registered...")
                progressDialog.dismiss()
                startActivity(Intent(this@Register_Using_Email_Activity, Log_In_Using_Email_Activity::class.java))
                finishAffinity()
            }.addOnFailureListener { e ->
                Log.e(TAG, "updateUserInfo", e)
                Utils.toast(this, "Failed to save user info due to ${e.message}")
            }
    }
}
//method used from YouTube
//https://youtu.be/TwHmrZxiPA8?si=KL-4IHmyOM0_qlol
//SmallAcademy