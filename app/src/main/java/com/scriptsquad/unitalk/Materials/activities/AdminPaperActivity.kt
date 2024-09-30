package com.scriptsquad.unitalk.Materials.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.scriptsquad.unitalk.R
import com.scriptsquad.unitalk.Utilities.Utils
import com.scriptsquad.unitalk.databinding.ActivityAdminStudentMaterialsBinding
import com.scriptsquad.unitalk.Materials.adapter.AdapterPaperCategoryAdmin
import com.scriptsquad.unitalk.Materials.model.ModelPaperCategory

// Class for Admin Paper Activity
class AdminPaperActivity : AppCompatActivity() {

    // Private lateinit variable for binding
    private lateinit var binding: ActivityAdminStudentMaterialsBinding

    // Private lateinit variable for adapter
    private lateinit var categoryArrayList: ArrayList<ModelPaperCategory>

    // Private lateinit variable for adapter
    private lateinit var adapterBooksCategoryAdmin: AdapterPaperCategoryAdmin
    // Private lateinit variable for Firebase Authentication
    private lateinit var firebaseAuth: FirebaseAuth

    // Override onCreate method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate layout and set content view
        binding = ActivityAdminStudentMaterialsBinding.inflate(layoutInflater)

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
        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance()

        // Load categories
        loadCategories()

        // Set on click listener for back button
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        // Set on click listener for add category button
        binding.addCategoryBtn.setOnClickListener {
            startActivity(Intent(this@AdminPaperActivity, AddCategoryPapersActivity::class.java))
        }
        // Set on click listener for add PDF FAB
        binding.addPdfFab.setOnClickListener {
            // Start Add Papers Activity
            startActivity(Intent(this@AdminPaperActivity, AddPapersActivity::class.java))
        }
        // Set text changed listener for search edit text
        binding.searchEt.addTextChangedListener(object :TextWatcher{
            // Override beforeTextChanged method
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                try {
                    // Filter adapter
                    adapterBooksCategoryAdmin.filter.filter(s)
                }catch (e:Exception){
                    // Show toast message if failed to search
                    Utils.toast(this@AdminPaperActivity,"Failed to search due to ${e.message}")
                }

            }
            // Override afterTextChanged method
            override fun afterTextChanged(s: Editable?) {

            }

        })


    }
    // Private function to load categories
    private fun loadCategories() {

        // Initialize category arrayList
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
                    // Get model from data snapshot
                    val model = ds.getValue(ModelPaperCategory::class.java)

                    // Add model to category array list
                    categoryArrayList.add(model!!)

                }

                // Set adapter
                adapterBooksCategoryAdmin =
                   AdapterPaperCategoryAdmin(this@AdminPaperActivity, categoryArrayList)

                // Set adapter to recyclerView
                binding.categoriesRv.adapter = adapterBooksCategoryAdmin


            }
            // Override onCancelled method
            override fun onCancelled(error: DatabaseError) {

            }

        })


    }


}