package com.scriptsquad.unitalk.Materials.activities

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.scriptsquad.unitalk.R
import com.scriptsquad.unitalk.Utilities.Utils
import com.scriptsquad.unitalk.Notes.MyApplication
import com.scriptsquad.unitalk.databinding.ActivityPaperDetailBinding
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.FileOutputStream

// Class for Paper Detail Activity
class PaperDetailActivity : AppCompatActivity() {

    // Private lateinit variable for binding
    private lateinit var binding:ActivityPaperDetailBinding

    // Companion object for TAG
    private companion object {
        private const val TAG = "PDF_DETAIL_PAPER_TAG"
    }
    // Private variables to store paper ID, title, URL, and image URL
    private var paperId = ""
    private var paperTitle = ""
    private var paperUrl = ""
    private var paperImageUrl = ""

    // Private lateinit variables for progress dialog and Firebase Authentication
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth

    // Override onCreate method
    override fun onCreate(savedInstanceState: Bundle?) {
        // Inflate layout and set content view
        binding=ActivityPaperDetailBinding.inflate(layoutInflater)
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

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance()
        // Get paper ID from intent
      paperId = intent.getStringExtra("paperId")!!

        // Initialize progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        // Set on click listener for back button
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Handle read button click
        binding.readBookBtn.setOnClickListener {
            val intent = Intent(this@PaperDetailActivity, PaperPdfViewActivity::class.java)
            intent.putExtra("paperId", paperId)
            startActivity(intent)
        }
        // Set on click listener for download button
        binding.downloadBookBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Log debug message
                Log.d(TAG, "onCreate: STORAGE PERMISSION is already granted")
                // Download paper
                downloadPaper()
            } else {
                // Log debug message
                Log.d(TAG, "onCreate: STORAGE PERMISSION not granted, LETS request it")
                requestStoragePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
        // Load paper detail
        loadBookDetail()

    }
    // Request storage permission
    private val requestStoragePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            // Allows to check if granted or not
            if (isGranted) {
                Log.d(TAG, "onCreate: STORAGE PERMISSION not granted")
            } else {
                // Log debug message
                Log.d(TAG, "onCreate: STORAGE PERMISSION not granted")
                // Show error toast
                MotionToast.createColorToast(
                    this,
                    "Failed",
                    "Permission Denied",
                    MotionToastStyle.WARNING,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular)
                )

            }
        }
    // Private function to download paper
    private fun downloadPaper() {
        Log.d(TAG, "downloadBook: Downloading Paper")
        // Progress bar
        progressDialog.setMessage("Download Book")
        progressDialog.show()

        // Can download book from firebase storage storage url
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(paperUrl)
        storageReference.getBytes(Utils.MAX_BYTES_PDF)
            .addOnSuccessListener { bytes ->
                // Log debug message
                Log.d(TAG, "downloadBook: Book downloaded...")
                saveToDownloadsFolder(bytes)
            }
            .addOnFailureListener { e ->
                // Dismiss progress dialog
                progressDialog.dismiss()
                // Log error message
                Log.d(TAG, "downloadBook: Failed to download book due to ${e.message}")
                // Show error toast
                MotionToast.createColorToast(
                    this,
                    "Failed",
                    "Failed to Download due to ${e.message}",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular)
                )
            }

    }
    // Private function to save paper to downloads folder
    private fun saveToDownloadsFolder(bytes: ByteArray?) {
        // Log debug message
        Log.d(TAG, "saveToDownloadsFolder: saving downloaded book")
        // Create file name with extension
        val nameWithExtension = "${paperTitle + System.currentTimeMillis()}.pdf"

        try {
            // Get downloads folder
            val downloadsFolder =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            // Create folder if not exist
            downloadsFolder.mkdirs() // Create folder if not exist
            val filepath = downloadsFolder.path + "/" + nameWithExtension
            // Write bytes to file
            val out = FileOutputStream(filepath)
            out.write(bytes)
            out.close()
            // Show success toast
            MotionToast.createColorToast(
                this,
                "Successfully",
                "Download Successfully",
                MotionToastStyle.SUCCESS,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular)
            )
            // Log debug message
            Log.d(TAG, "saveToDownloadsFolder: Save to Download Folder")
            // Dismiss progress dialog
            progressDialog.dismiss()


        } catch (e: Exception) {
            // Dismiss progress dialog
            progressDialog.dismiss()
            // Log error message
            Log.e(TAG, "saveToDownloadsFolder: failed to save due to ${e.message}")
            // Show error toast
            MotionToast.createColorToast(
                this,
                "Failed",
                "Failed to Download due to ${e.message}",
                MotionToastStyle.ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular)
            )

        }
    }
    // Private function to load paper detail
    private fun loadBookDetail() {

        Log.d(TAG, "loadPaperDetail: ")

        //Books >bookId >Details
        val ref = FirebaseDatabase.getInstance().getReference("Papers")
        // Get paper detail from database
        ref.child(paperId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                // Override onDataChange method
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Get paper detail
                    val categoryId = "${snapshot.child("categoryId").value}"
                    // Format timestamp date
                    val timestamp = snapshot.child("timestamp").value as Long
                    paperTitle = "${snapshot.child("title").value}"
                    val uid = "${snapshot.child("uid").value}"
                    paperUrl= "${snapshot.child("url").value}"
                    paperImageUrl = "${snapshot.child("imageUrl").value}"


                    val formattedDate = MyApplication.formatTimestampDate(timestamp)

                    // Load category papers
                    MyApplication.loadCategoryPapers(categoryId, binding.categoryTv)


                    // Load bool Image
                    MyApplication.loadBookImage(
                        paperImageUrl,
                        binding.progressBar,
                        baseContext,
                        binding.booksImageIv
                    )

                    // Load pdf size
                    MyApplication.loadPdfSize(paperUrl, "$title", binding.sizeTv)

                    // Set paper detail to views
                    binding.titleTv.text = paperTitle
                    binding.dateTv.text = formattedDate
                    // Log debug message
                    Log.d(TAG, "onDataChange: Successfully")

                }
                // Load category papers
                override fun onCancelled(error: DatabaseError) {

                    // Log error message
                    Log.e(TAG, "onCancelled: failed due to $error")

                }

            })

    }

}