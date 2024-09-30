package com.scriptsquad.unitalk.StartPage

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.database.FirebaseDatabase
import com.scriptsquad.unitalk.R
import com.scriptsquad.unitalk.Utilities.Utils
import com.scriptsquad.unitalk.databinding.ActivityLoginPhoneBinding
import java.util.concurrent.TimeUnit

//method used from YouTube
//https://youtu.be/H_maapn4Q3Q?si=_1siEM622Nqtcr-s
//channel: TECH_WORLD
// Class representing the phone login activity
class Log_In_Using_Phone_Activity : AppCompatActivity() {

    // Late-initialized variables for the activity's binding and Firebase authentication
    private lateinit var binding:ActivityLoginPhoneBinding

    // Companion object to hold the TAG for logging
    private companion object{
        private const val TAG="Phone_LOGIN_TAG"
    }

    // Late-initialized variables for the progress dialog, Firebase authentication, and phone verification
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private var forceRefreshingToken:ForceResendingToken?=null
    private lateinit var mCallbacks :OnVerificationStateChangedCallbacks
    private var mVerificationId:String?=null

    // Called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        binding=ActivityLoginPhoneBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Set the initial visibility of the phone input and OTP input layouts
        binding.phoneInputRl.visibility= View.VISIBLE
        binding.otpInputRl.visibility=View.GONE


progressDialog=ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)


        firebaseAuth=FirebaseAuth.getInstance()

        // Set up the phone login callbacks
        phoneLoginCallBacks()

        // handle toolBack btn
        binding.ToolbarBackBtn.setOnClickListener {
           onBackPressedDispatcher.onBackPressed()
        }

        // Send OTP button click listener
        binding.sendOtpBtn.setOnClickListener {
            validateData()
        }

        // Resend OTP text view click listener
        binding.resendOtpTv.setOnClickListener {
            resendVerificationCode(forceRefreshingToken!!)
        }
        // Verify OTP button click listener
        binding.verifyOtpBtn.setOnClickListener {
            val otp = binding.otpEt.text.toString().trim()
            Log.d(TAG,"onCreate: otp : $otp")

            // Check if the OTP is empty
            if (otp.isEmpty()){
                binding.otpEt.error="Enter OTP"
                binding.otpEt.requestFocus()
            }
            // Check if the OTP length is less than 6
            else if (otp.length <6){
                binding.otpEt.error = "OTP length must be 6 characters long"
                binding.otpEt.requestFocus()
            }
            else{
                verifyPhoneNumberWithCode(mVerificationId,otp)
            }

        }

    }

    // Variables to store the user's phone code and phone number
    private var phoneCode=""
    private var phoneNumber=""
    private var phoneNumberWithCode=""

    // Method to validate the user's input data
    private fun validateData(){
phoneCode=binding.phoneCodeTil.selectedCountryCodeWithPlus
        phoneNumber=binding.phoneNumber.text.toString().trim()
        phoneNumberWithCode=phoneCode + phoneNumber

        // Log the user's phone code and phone number for debugging purposes
        Log.d(TAG,"validateData: phoneCode: $phoneCode")
        Log.d(TAG,"validateData: phoneNumber: $phoneNumber")
        Log.d(TAG,"validateData: phoneNumberWithCode: $phoneNumberWithCode")

        if (phoneNumber.isEmpty()){
            binding.phoneNumber.error="Enter Phone Number"
            binding.phoneNumber.requestFocus()
        }
        else{
            startPhoneNumberVerification()
        }


    }
    //method used from YouTube
    //https://youtu.be/3V3W3HjYzog?si=T4VIT3OJocWrVyEM
    //channel: Papaya Coders
    // Method to start the phone number verification
    private fun startPhoneNumberVerification(){
        Log.d(TAG,"startPhoneNumberVerification: ")
        progressDialog.setTitle("Sending OTP to $phoneNumberWithCode")
        progressDialog.setMessage("Please Wait...")
        progressDialog.show()
        // set phoneAuth options
        val option = PhoneAuthOptions.newBuilder(firebaseAuth) // fireBase Instance
            .setPhoneNumber(phoneNumberWithCode) // phone Number with country code r.g. +92********
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and Unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(mCallbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(option)
    }

    // Method to set up the phone login callbacks
    private fun phoneLoginCallBacks(){
        Log.d(TAG,"phoneLoginCallBacks:")

        mCallbacks = object:OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
               Log.d(TAG,"onVerificationCompleted: ")

                   // This callBack will invoke in two Situations
                //1 - Instantly verification. In some cases the phone number
                // can be instantly verified without need to send or Enter OTP

                //2 - Auto_retrieval. On some devices Google play Services can automatically
            //    detect the incoming SMS and perform verification without
              //          user Action
                signInWithPhoneCredential(credential)

            }



            override fun onVerificationFailed(e: FirebaseException) {
                Log.d(TAG,"Verification Failed $e")
               // This call back is invoked when there is invalid request for verification
                // like phone number format is not valid
                progressDialog.dismiss()
                Utils.toast(this@Log_In_Using_Phone_Activity, "${e.message}")
            }

            override fun onCodeSent(verificationId: String, token: ForceResendingToken) {
                Log.d(TAG,"onCodeSent: verificationId: $verificationId")
               //The SMS verification code has sent to provider phone number, we
                // now need to ask the user to enter the code and then construct a
                // credential by combing the code with a verification ID
                mVerificationId =verificationId
                forceRefreshingToken = token
                progressDialog.dismiss()
                binding.phoneInputRl.visibility=View.GONE
                binding.otpInputRl.visibility=View.VISIBLE

                Utils.toast(this@Log_In_Using_Phone_Activity, "OTP is sent to $phoneNumberWithCode ")

                binding.LogInLabelTv.text = getString(
                    R.string.please_type_the_verification_code_sent_to,
                    phoneNumberWithCode
                )
            }


            override fun onCodeAutoRetrievalTimeOut(p0: String) {

            }

        }
    }

    private fun signInWithPhoneCredential(credential: PhoneAuthCredential) {

    }
    //method used from YouTube
    //https://youtu.be/dI8mMpL8dmo?si=N-bqVQNRbhx8OPQX
    //channel: Chirag Kachhadiya
  private fun verifyPhoneNumberWithCode(verificationId: String?, otp: String) {
      Log.d(TAG,"verifyPhoneNumberWithCode: verificationId: $verificationId")
      Log.d(TAG,"verifyPhoneNumberWithCode: OTP: $otp")
      progressDialog.setMessage("Verifying OTP")
      progressDialog.show()


      val credential = PhoneAuthProvider.getCredential(verificationId!!,otp)
      signInWithPhoneAuthCredential(credential)
  }

    private fun resendVerificationCode(token: ForceResendingToken?){
        Log.d(TAG,"resendVerificationCode: ")
        progressDialog.setTitle("ReSending OTP to $phoneNumberWithCode")
        progressDialog.show()
        // set phoneAuth options
        val option = PhoneAuthOptions.newBuilder(firebaseAuth) // fireBase Instance
            .setPhoneNumber(phoneNumberWithCode) // phone Number with country code r.g. +92********
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and Unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(mCallbacks)
            .setForceResendingToken(token!!)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(option)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential){
        Log.d(TAG,"signInWithPhoneAuthCredentials: ")
        progressDialog.setMessage("Logging In")
        progressDialog.show()
        firebaseAuth.signInWithCredential(credential).addOnSuccessListener {authResutl ->
            Log.d(TAG,"signInWithPhoneAuthCredential: Success")
                // firebase auth with [hone credential is success , check if user is new or existing
            if (authResutl.additionalUserInfo!!.isNewUser){
                Log.d(TAG,"signInWithPhoneAuthCredential: New User, Account Created")
                // New user, Account created Let's save user info to firebase realtime database
                UpdateUserInfoDb()
            }
            else{
                Log.d(TAG,"signInWithPhoneAuthCredential :Existing User, Logged In")
                //Existing user Signed In. No need to save user info to firebase ,Start Activity
                startActivity(Intent(this, Main_Home_Screen::class.java))
                finishAffinity()
            }
        }
            .addOnFailureListener {e ->
            Log.e(TAG,"signInWithPhoneAuthCredential" , e)
            progressDialog.dismiss()
                Utils.toast(this@Log_In_Using_Phone_Activity, "Failed To login due to ${e.message}")

            }
    }

    //method used from YouTube
    //https://youtu.be/DWIGAkYkpg8?si=um9GgnsGWc9G7KAB
    //channel: Android Knowledge
    private fun UpdateUserInfoDb(){
        Log.d(TAG,"updateUserInfoDb")
        progressDialog.setMessage("Saving user Info")
        progressDialog.show()

        val timestamp = Utils.getTimestamp()
        val registeredUserUid = firebaseAuth.uid

        val hashMap = hashMapOf<String,Any?>()
        hashMap["name"] =""
        hashMap["phoneCode"] ="$phoneCode"
        hashMap["phoneNumber"]="$phoneNumber"
        hashMap["profileImageURl"] =""
        hashMap["dob"] = ""
        hashMap["userType"] ="Phone"
        hashMap["typingTo"] =""
        hashMap["timestamp"]=timestamp
        hashMap["onlineStatus"]=true
        hashMap["email"]=""
        hashMap["uid"]="$registeredUserUid"
        hashMap["userMode"]="${Utils.USER_MODE}"

        val ref  = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(registeredUserUid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG,"updateUserInfoDb: User Info Saved")
                progressDialog.dismiss()
                startActivity(Intent(this@Log_In_Using_Phone_Activity, Main_Home_Screen::class.java))
                finishAffinity()
            }.addOnFailureListener {e ->
            Log.e(TAG,"updateUserInfoDb")
                progressDialog.dismiss()
                Utils.toast(this@Log_In_Using_Phone_Activity, "Failed to save user info due to ${e.message}")

            }
    }

}