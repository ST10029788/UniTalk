package com.scriptsquad.unitalk.Materials.activities


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.scriptsquad.unitalk.Utilities.Utils
import com.scriptsquad.unitalk.databinding.ActivityStudentMaterialsAdminBinding
import com.scriptsquad.unitalk.Materials.adapter.AdapterPaperCategory
import com.scriptsquad.unitalk.Materials.model.ModelPaperCategory
// Class for Paper User Activity
class PaperUserActivity : AppCompatActivity() {

    // Private lateinit variable for binding
    private lateinit var binding: ActivityStudentMaterialsAdminBinding
    // Private lateinit variable for category array list
    private lateinit var categoryArrayList: ArrayList<ModelPaperCategory>
    // Private lateinit variable for adapter
    private lateinit var adapterBooksCategoryAdmin: AdapterPaperCategory
    // Private lateinit variable for Firebase Authentication
    private lateinit var firebaseAuth: FirebaseAuth

    // Override onCreate method
    override fun onCreate(savedInstanceState: Bundle?) {

        // Inflate layout and set content view
        binding = ActivityStudentMaterialsAdminBinding.inflate(layoutInflater)
        // Call super onCreate method
        super.onCreate(savedInstanceState)
        // Set content view
        setContentView(binding.root)
        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance()
        // Set on click listener for back button
        binding.toolbarBackbtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        // Set text changed listener for search edit text
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            // Override beforeTextChanged method
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            // Override onTextChanged method
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                try {
                    // Filter adapter
                    adapterBooksCategoryAdmin.filter.filter(s)
                } catch (e: Exception) {
                    // Show toast message if failed to search
                    Utils.toast(this@PaperUserActivity, "Failed to search due to ${e.message}")
                }

            }
            // Override afterTextChanged method
            override fun afterTextChanged(s: Editable?) {

            }

        })

        // Load categories
        loadCategories()


    }
    // Private function to load categories
    private fun loadCategories() {

        // Init arrayList
        categoryArrayList = ArrayList()

        // Get reference to Firebase database
        val ref = FirebaseDatabase.getInstance().getReference("CategoriesPapers")
        // Add value event listener to database reference
        ref.addValueEventListener(object : ValueEventListener {
            // Override onDataChange method
            override fun onDataChange(snapshot: DataSnapshot) {

                // Clear category array list
                categoryArrayList.clear()

                // Iterate through data snapshot
                for (ds in snapshot.children) {
                    // Get data as model
                    val model = ds.getValue(ModelPaperCategory::class.java)

                    // Add to array list
                    categoryArrayList.add(model!!)

                }

                // Set adapter
                adapterBooksCategoryAdmin =
                    AdapterPaperCategory(this@PaperUserActivity, categoryArrayList)

                // Set adapter to recyclerView
                binding.categoriesRv.adapter = adapterBooksCategoryAdmin


            }

            // Override onCancelled method
            override fun onCancelled(error: DatabaseError) {

            }

        })


    }

}

