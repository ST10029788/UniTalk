package com.scriptsquad.unitalk.Account

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.scriptsquad.unitalk.StartPage.Main_Activity
import com.scriptsquad.unitalk.Utilities.Utils
import com.scriptsquad.unitalk.databinding.ActivityDeleteAccountBinding

class Delete_Account_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityDeleteAccountBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var firebaseUser: FirebaseUser? = null
    private lateinit var progressDialog: ProgressDialog

    private companion object {
        private const val TAG = "DELETE_ACCOUNT_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser
        progressDialog = ProgressDialog(this@Delete_Account_Activity)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.toolbarBackBtn.setOnClickListener {
            onBackPressed()
        }
        binding.deleteAccountBtn.setOnClickListener {
            deleteAccount()
        }


    }

    private fun deleteAccount() {
        Log.d(TAG, "delete Account: ")
        progressDialog.setMessage("Deleting user Account...")
        progressDialog.show()

        val myUid = firebaseAuth.uid

        firebaseUser!!.delete().addOnSuccessListener {
            Log.d(TAG, "User deleted: Successfully ")
            progressDialog.dismiss()
            // Remove user Ads
            val refUserAds = FirebaseDatabase.getInstance().getReference("Ads")
            refUserAds.orderByChild("uid").equalTo(myUid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (ds in snapshot.children) {
                            ds.ref.removeValue()
                        }
                        progressDialog.setMessage("Deleting user data...")

                        // remove userData, DB > Users > UserId
                        val refUser = FirebaseDatabase.getInstance().getReference("Users")
                        refUser.child(myUid!!).removeValue().addOnSuccessListener {
                            Log.d(TAG, "Data deleted")
                            progressDialog.dismiss()

                        }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Failed to delete data: ", e)
                                progressDialog.dismiss()
                                Utils.toast(
                                    this@Delete_Account_Activity,
                                    "Failed to delete data due to ${e.message}"
                                )
                                startMainActivity()
                            }


                    }

                    override fun onCancelled(error: DatabaseError) {
                        startMainActivity()
                    }
                })



            Utils.toast(this@Delete_Account_Activity, "Account deleted successfully: ")
        }.addOnFailureListener { e ->
            Log.d(TAG, "Failed to delete User: ", e)
            progressDialog.dismiss()
            Utils.toast(
                this@Delete_Account_Activity,
                "Failed to delete your Account due to ${e.message}"
            )
        }

    }

    private fun startMainActivity() {
        startActivity(Intent(this@Delete_Account_Activity, Main_Activity::class.java))
        finishAffinity()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startMainActivity()
      
    }
}
//method used from YouTube
//https://youtu.be/bDpTJJJo0Aw?si=HmBT0oHYO0HDkiMq
//channel: Coding Meet
