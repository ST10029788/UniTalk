package com.scriptsquad.unitalk.Materials.activities

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.scriptsquad.unitalk.Utilities.Utils
import com.scriptsquad.unitalk.databinding.ActivityAddCategoryPapersBinding
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
// Class for Add Category Papers Activity
class AddCategoryPapersActivity : AppCompatActivity() {

    // Private lateinit variable for binding
    private lateinit var binding: ActivityAddCategoryPapersBinding

    // Private lateinit variable for Firebase Authentication
    private lateinit var firebaseAuth: FirebaseAuth

    // Private lateinit variable for Progress Dialog
    private lateinit var progressDialog: ProgressDialog
    // Override onCreate method
    override fun onCreate(savedInstanceState: Bundle?) {

        // Inflate layout and set content view
        binding = ActivityAddCategoryPapersBinding.inflate(layoutInflater)

        // Call super onCreate method
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize Progress Dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setMessage("Adding Category")
        progressDialog.setCanceledOnTouchOutside(false)

        // Set on click listener for back button
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Set on click listener for submit button
        binding.submitBtn.setOnClickListener {
            validateData()
        }

    }
    // Private variable to store category
    private var category = ""

    // Private function to validate data
    private fun validateData() {

        // Get category from edit text
        category = binding.categoryEt.text.toString().trim()

        // Validate data
        if (category.isEmpty()) {

            // Show error toast if category is empty
            MotionToast.createColorToast(
                this@AddCategoryPapersActivity,
                "Error",
                "Field cannot be Empty",
                MotionToastStyle.ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(
                    this@AddCategoryPapersActivity,
                    www.sanju.motiontoast.R.font.helvetica_regular
                )
            )
        } else {
            addCategoryFirebase()
        }


    }
    // Private function to add category to Firebase
    private fun addCategoryFirebase() {

        // Show progress dialog
        progressDialog.show()

        // Get timestamp
        val timestamp = Utils.getTimestamp()

        // Create hash map to store data
        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$timestamp"
        hashMap["category"] = category
        hashMap["timestamp"] = timestamp
        hashMap["uid"] = "${firebaseAuth.uid}"

        // Get reference to Firebase Database
        val ref = FirebaseDatabase.getInstance().getReference("CategoriesPapers")
        // Add data to Firebase Database
        ref.child("$timestamp")
            .setValue(hashMap)

            .addOnSuccessListener {

                // Dismiss progress dialog on success
                progressDialog.dismiss()
                MotionToast.createColorToast(
                    this@AddCategoryPapersActivity,
                    "Successfully",
                    "Added Category successfully",
                    MotionToastStyle.SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(
                        this@AddCategoryPapersActivity,
                        www.sanju.motiontoast.R.font.helvetica_regular
                    )
                )

            }
            .addOnFailureListener { e ->

                progressDialog.dismiss()

                // Show success toast
                MotionToast.createColorToast(
                    this@AddCategoryPapersActivity,
                    "Failed",
                    "Failed to add due to ${e.message}",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(
                        this@AddCategoryPapersActivity,
                        www.sanju.motiontoast.R.font.helvetica_regular
                    )
                )

            }


    }
}