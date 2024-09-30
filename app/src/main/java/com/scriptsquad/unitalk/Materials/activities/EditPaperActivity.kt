package com.scriptsquad.unitalk.Materials.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.scriptsquad.unitalk.R
import com.scriptsquad.unitalk.databinding.ActivityEditPaperBinding
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

// Class for Edit Paper Activity
class EditPaperActivity : AppCompatActivity() {

    // Private lateinit variable for binding
    private lateinit var binding: ActivityEditPaperBinding
    // Private lateinit variable for progress dialog
    private lateinit var progressDialog: ProgressDialog
    // Private lateinit variables for category title and ID array lists
    private lateinit var categoryTitleArrayList: ArrayList<String>
    // Private lateinit variables for category title and ID array lists
    private lateinit var categoryIdArrayList: ArrayList<String>
    // Companion object for TAG
    private companion object {
        private const val TAG = "PAPER_EDIT"
    }
    // Paper ID from intent started from AdapterPdfAdmin
    private var paperId = ""

    // Override onCreate method
    override fun onCreate(savedInstanceState: Bundle?) {
        // Inflate layout and set content view
        binding = ActivityEditPaperBinding.inflate(layoutInflater)
        // Call super onCreate method
        super.onCreate(savedInstanceState)
        // Enable edge to edge
        enableEdgeToEdge()
        // Set content view
        setContentView(binding.root)
        // Set on apply window insets listener
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Get paper ID from intent
        paperId = intent.getStringExtra("paperId")!!
        // Initialize progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait....")
        progressDialog.setCanceledOnTouchOutside(false)
        // Load categories and paper info
        loadCategories()
        loadPaperInfo()
        // Set on click listener for back button
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        // Set on click listener for category text view
        binding.categoryTv.setOnClickListener {
            categoryDialog()
        }
        // Set on click listener for submit button
        binding.submitBtn.setOnClickListener {
            validateData()
        }

    }

    // Private variable to store title
    private var title = ""

    // Private function to validate data
    private fun validateData() {

        // Get title from edit text
        title = binding.titleEt.text.toString().trim()

        // Validate data
        if (title.isEmpty()) {
            // Show error message if title is empty
            binding.titleEt.requestFocus()
            binding.titleEt.error = "Enter Title"
        }
         else if (selectedCategoryId.isEmpty()) {
            // Show warning toast if category is not selected
            MotionToast.createColorToast(
                this,
                "Warning",
                "Select Category",
                MotionToastStyle.WARNING,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.SHORT_DURATION,
                ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helveticabold)
            )
        }
        else{
            // Update PDF if data is valid
            updatePdf()
        }

    }
    // Private function to load paper info
    private fun loadPaperInfo() {
        // Log debug message
        Log.d(TAG, "loadPaperInfo: Loading Paper Info")
        // Get reference to Firebase database
        val ref = FirebaseDatabase.getInstance().getReference("Papers")
        // Get paper info from database
        ref.child(paperId)
            .addValueEventListener(object:ValueEventListener{
                // Override onDataChange method
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Get paper info
                    selectedCategoryId = snapshot.child("categoryId").value as String
                    val title = snapshot.child("title").value as String

                    // Set title to edit text
                    binding.titleEt.setText(title)

                    // Load category info using category Id
                    Log.d(TAG, "onDataChange: Loading book category info")
                    val refBookCategory = FirebaseDatabase.getInstance().getReference("CategoriesPapers")
                    refBookCategory.child(selectedCategoryId)
                        .addListenerForSingleValueEvent(object:ValueEventListener{

                            override fun onDataChange(snapshot: DataSnapshot) {
                                // Get category
                                val category = snapshot.child("category").value

                                // Set to textView
                                binding.categoryTv.text = category.toString()
                            }
                            // Override onCancelled method
                            override fun onCancelled(error: DatabaseError) {

                            }

                        })

                }
                // Override onCancelled method
                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
    // Private variables to store selected category ID and title
    private var selectedCategoryId = ""
    private var selectedCategoryTitle = ""
    // Private function to load categories
    private fun loadCategories() {
        // Log debug message
        Log.d(TAG, "loadCategories: loading categories:")
        // Initialize category title and ID array lists
        categoryIdArrayList = ArrayList()
        categoryTitleArrayList = ArrayList()
        // Get reference to Firebase database
        val ref = FirebaseDatabase.getInstance().getReference("CategoriesPapers")
        // Add value event listener to database reference
        ref.addValueEventListener(object : ValueEventListener {
            // Override onDataChange method
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear list before adding data
                categoryTitleArrayList.clear()
                categoryIdArrayList.clear()
                // Iterate through data snapshot
                for (ds in snapshot.children) {
                    // Get category ID and title
                    val id = "${ds.child("id").value}"
                    val category = "${ds.child("category").value}"
                    // Add category ID and title to array lists
                    categoryIdArrayList.add(id)
                    categoryTitleArrayList.add(category)
                    // Log debug message
                    Log.d(TAG, "onDataChange: Category Id :$id")
                    Log.d(TAG, "onDataChange: Category: $category")

                }
            }
            // Override onCancelled method
            override fun onCancelled(error: DatabaseError) {

            }

        })


    }

    // Private function to update PDF
    private fun updatePdf(){
        // Log debug message
        Log.d(TAG, "updatePaper: Starting updating paper info....")

        // Show progress dialog
        progressDialog.setMessage("Updating Materials info")
        progressDialog.show()
        // Create hash map to store updated data
        val hashMap = HashMap<String,Any>()
        hashMap ["title"] = "$title"
        hashMap["categoryId"] = "$selectedCategoryId"

        // Get reference to Firebase database
        val ref = FirebaseDatabase.getInstance().getReference("Papers")
        // Update paper info in database
        ref.child(paperId)
            .updateChildren(hashMap)
            // Add on success listener
            .addOnSuccessListener {
                // Dismiss progress dialog
                progressDialog.dismiss()
                // Log debug message
                Log.d(TAG, "updatePaper: Update Successfully...")
                // Show success toast
                MotionToast.createColorToast(
                    this,
                    "Success",
                    "Updated Successfully",
                    MotionToastStyle.SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helveticabold)
                )
            }
            // Add on failure listener
            .addOnFailureListener {e ->
                // Log error message
                Log.d(TAG, "updatePdf:Failed to Update due to ${e.message} ")
                // Dismiss progress dialog
                progressDialog.dismiss()
                // Show error toast
                MotionToast.createColorToast(
                    this,
                    "Error",
                    "Failed to Update due to ${e.message}",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helveticabold)
                )

            }

    }

    // Private function to show category dialog
    private fun categoryDialog() {
        // Create array of category titles
        val categoryArray = arrayOfNulls<String>(categoryTitleArrayList.size)
        for (i in categoryTitleArrayList.indices) {
            categoryArray[i] = categoryTitleArrayList[i]
        }
        // Create alert dialog builder
        val builder = AlertDialog.Builder(this)
        // Set title and items to dialog
        builder.setTitle("Choose Category")
            .setItems(categoryArray) { _, position ->

                // Handle clicks and saved clicked category id and title
                selectedCategoryId = categoryIdArrayList[position]
                selectedCategoryTitle = categoryTitleArrayList[position]

                //Set  textView

                binding.categoryTv.text = selectedCategoryTitle
            }
            .show() // Show dialog

    }

}