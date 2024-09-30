package com.scriptsquad.unitalk.About_Page

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.storage.FirebaseStorage
import com.scriptsquad.unitalk.R
import com.scriptsquad.unitalk.Utilities.Utils
import com.scriptsquad.unitalk.databinding.ActivityAboutDetailBinding

class About_Details_Activity : AppCompatActivity() {

    // Companion object to hold constants
    private companion object {
        private const val TAG = "ABOUT_DETAIL"
    }

    // Lateinit variable to hold the binding for the activity layout
    private lateinit var binding: ActivityAboutDetailBinding

    // Variable to hold the type of about detail
    private var aboutType = ""
    // Override the onCreate method
    override fun onCreate(savedInstanceState: Bundle?) {

        // Inflate the activity layout
        binding = ActivityAboutDetailBinding.inflate(layoutInflater)

        // Call the superclass onCreate method
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display
        enableEdgeToEdge()

        // Set the content view to the inflated layout
        setContentView(binding.root)

        // Set a listener to handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get the about type from the intent
        aboutType = intent.getStringExtra("Detail")!!

        // Enable JavaScript in the web view
        @JavascriptInterface
        binding.webView.settings.javaScriptEnabled = true

        // Set the web view client
        binding.webView.webViewClient = WebViewClient()

        // Initially hide the web view
        binding.webView.visibility = View.GONE

        // Handle the about type
        when (aboutType) {
            // Load the resume
            "resume" -> {
                loadResume()
            }

            // Load the GitHub page
            "github" -> {
                loadGitHub()
            }

        }

        // Set a click listener for the back button
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }
    // Method to load the resume
    private fun loadResume() {

        // Show the progress bar and PDF view
        binding.progressBar.visibility = View.VISIBLE
        binding.pdfView.visibility = View.VISIBLE
        binding.titleTv.text = getString(R.string.resume)

        // URL of the PDF file
        val pdfUrl =
            "https://firebasestorage.googleapis.com/v0/b/unitalk-648b1.appspot.com/o/OPSC7312POE.docx?alt=media&token=a38ff476-da37-4a37-b441-ca380416f8fe"

        // Get a reference to the PDF file in Firebase Storage
        val reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)

        // Get the bytes of the PDF file
        reference.getBytes(Utils.MAX_BYTES_PDF)
            .addOnSuccessListener { bytes ->
                Log.d(TAG, "loadBookFromUrl: pdf got from url $pdfUrl")

                // Load the PDF file into the PDF view
                binding.pdfView.fromBytes(bytes)
                    .swipeHorizontal(false)
                    .onPageChange { page, pageCount ->
                        //set current and total pages toolbar subtitle
                        val currentPage = page + 1 // page starts from 0 so we add 1

                        Log.d(TAG, "loadBookFromUrl: $currentPage/$pageCount")
                    }
                    .onError { t ->
                        Log.e(TAG, "loadBookFromUrl: ${t.message}")
                    }
                    .onPageError { page, t ->
                        Utils.toast(this, "Error at page: $page")
                        Log.d(TAG, "loadBookFromUrl: ${t.message}")
                    }

                    .onError { error ->
                        error.message?.let {
                            Utils.toast(this, it)
                        }

                    }
                    .load()

                // Hide the progress bar
                binding.progressBar.visibility = View.GONE


                //load pdf


            }
            .addOnFailureListener { e ->
                Log.e(TAG, "loadBookFromUrl: Failed to get url due to ${e.message}")
                binding.progressBar.visibility = View.GONE
            }

    }

    // Method to load the GitHub page
    private fun loadGitHub() {
        // Show the progress bar
        binding.progressBar.visibility = View.VISIBLE

        // Set the title text
        binding.titleTv.text = getString(R.string.git_hub)
        try {

            // URL to load in the web view
            val urlToLoad = "https://github.com/ST10029788/UniTalk.git"
            binding.webView.loadUrl(urlToLoad)
            binding.webView.visibility = View.GONE

            // Hide the progress bar and show the web view after 2 seconds
            android.os.Handler().postDelayed({
                binding.progressBar.visibility = View.GONE
                binding.webView.visibility = View.VISIBLE
            }, 2000)
        } catch (e: Exception) {
            Log.e(TAG, "loadGitHub: ${e.message}")
        }


    }


}