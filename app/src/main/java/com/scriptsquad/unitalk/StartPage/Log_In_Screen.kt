package com.scriptsquad.unitalk.StartPage

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.scriptsquad.unitalk.R
import com.scriptsquad.unitalk.Utilities.Utils
import com.scriptsquad.unitalk.databinding.ActivityLogInBinding
import java.lang.Exception

class Log_In_Screen : AppCompatActivity() {

    private companion object {
        private const val TAG = "LOGIN_OPTIONS_TAG"
    }

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var binding: ActivityLogInBinding
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        createDefaultAdminAccount()


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)


//
        binding.LogInEmailBtn.setOnClickListener {
            startActivity(Intent(this@Log_In_Screen, Log_In_Using_Email_Activity::class.java))
        }

        binding.LogInGoogleBtn.setOnClickListener {
            beginGoogleLogin()
        }
        binding.LogInPhoneBtn.setOnClickListener {
            startActivity(Intent(this@Log_In_Screen, Log_In_Using_Phone_Activity::class.java))
        }

        binding.LogInGuestBtn.setOnClickListener {
            continueAsGuest()
        }
        // Redirect to sign-up screen when "Sign up here" is clicked
        binding.signupredirect.setOnClickListener {
            startActivity(Intent(this@Log_In_Screen, Register_Using_Email_Activity::class.java)) // Replace SignUpActivity with your actual sign-up activity class
        }

    }


    private fun createDefaultAdminAccount() {
        val email = "admin@varsitycollege.co.za"
        val password = "admin123"

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result.user!!.uid

                    // Assign the admin role to the user
                    val hashMap = HashMap<String, Any>()
                    hashMap["role"] = "admin"

                    val ref = FirebaseDatabase.getInstance().getReference("Users")
                    ref.child(uid).updateChildren(hashMap)
                        .addOnSuccessListener {
                            Log.d(TAG, "Default admin account created successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error creating default admin account", e)
                        }
                } else {
                    Log.e(TAG, "Error creating default admin account", task.exception)
                }
            }
    }
    private val email: String = "guestuser@gmail.com"
    private val password: String = "1234guest"

    private fun continueAsGuest() {
        Log.d(TAG, "loginUser")
        progressDialog.setTitle("Logging In...")
        progressDialog.setMessage("Loading...")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                Log.e(TAG, "loginUser: Logged In...")
                progressDialog.dismiss()
                startActivity(Intent(this@Log_In_Screen, Main_Home_Screen::class.java))
                finishAffinity()
            }.addOnFailureListener { e ->
                Log.e(TAG, "logInUser", e)
                progressDialog.dismiss()

                Utils.toast(this, "Unable to Login due to ${e.message}")
            }
    }


    private fun beginGoogleLogin() {
        Log.d(TAG, "beginGoogleLogin:")
        val googleSignInIntent = mGoogleSignInClient.signInIntent
        googleSignnInARL.launch(googleSignInIntent)
    }

    private val googleSignnInARL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(TAG, "googleSignInARL: ")
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "googleSignInARL: Account ID: ${account.id}")
                firebaseAuthWithGoogleAccount(account.idToken)
            } catch (e: Exception) {
                Log.d(TAG, "googleSignInARL", e)
                Utils.toast(this@Log_In_Screen, "${e.message}")
            }

        } else {
            Utils.toast(this@Log_In_Screen, "Cancelled...!")
        }

    }

    private fun firebaseAuthWithGoogleAccount(idToken: String?) {
        Log.d(TAG, "firebaseAuthWithGoogleAccount: idToken : $idToken")

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                if (authResult.additionalUserInfo!!.isNewUser) {
                    Log.d(TAG, "firebaseAuthWithGoogle: New User, Account created....")
                    updateUserInfoDb()
                } else {
                    Log.d(TAG, "firebaseAuthWithGoogle: Existing User, Logged In....")
                    startActivity(Intent(this@Log_In_Screen, Main_Home_Screen::class.java))
                    finishAffinity()
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "firebaseAuthWithGoogleAccount: ", e)
                Utils.toast(this@Log_In_Screen, "${e.message}")

            }
    }

    private fun updateUserInfoDb() {
        Log.d(TAG, "Saving User Info")

        progressDialog.setTitle("Saving User Info")
        progressDialog.show()

        val timestamp = Utils.getTimestamp()
        val registerUserEmail = firebaseAuth.currentUser?.email
        val registerUserUid = firebaseAuth.uid
        val name = firebaseAuth.currentUser?.displayName

        val hashMap = HashMap<String, Any?>()

        hashMap["name"] = "$name"
        hashMap["phoneCode"] = ""
        hashMap["phoneNumber"] = ""
        hashMap["profileImageURl"] = ""
        hashMap["dob"] = ""
        hashMap["userType"] = "Google"
        hashMap["typingTo"] = ""
        hashMap["timestamp"] = timestamp
        hashMap["onlineStatus"] = true
        hashMap["email"] = "$registerUserEmail"
        hashMap["uid"] = "$registerUserUid"
        hashMap["userMode"] = Utils.USER_MODE

        // set data to firebase

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(registerUserUid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "updatedUserInfoDb: User info saved")
                progressDialog.dismiss()
                startActivity(Intent(this, Main_Home_Screen::class.java))
                finishAffinity()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "updateUserInfoDb", e)
                Utils.toast(this, "Failed to save user info due to ${e.message}")
            }


    }

}