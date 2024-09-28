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

    private companion object {
        private const val TAG = "ABOUT_DETAIL"
    }

    private lateinit var binding: ActivityAboutDetailBinding

    private var aboutType = ""
    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityAboutDetailBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        aboutType = intent.getStringExtra("Detail")!!

        @JavascriptInterface
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = WebViewClient()
        binding.webView.visibility = View.GONE

        when (aboutType) {
            "resume" -> {
                loadResume()
            }

            "github" -> {
                loadGitHub()
            }

        }

        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun loadResume() {

        binding.progressBar.visibility = View.VISIBLE
        binding.pdfView.visibility = View.VISIBLE
        binding.titleTv.text = getString(R.string.resume)

        val pdfUrl =
            "https://firebasestorage.googleapis.com/v0/b/unitalk-648b1.appspot.com/o/OPSC7312POE.docx?alt=media&token=a38ff476-da37-4a37-b441-ca380416f8fe"

        val reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
        reference.getBytes(Utils.MAX_BYTES_PDF)
            .addOnSuccessListener { bytes ->
                Log.d(TAG, "loadBookFromUrl: pdf got from url $pdfUrl")


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

                binding.progressBar.visibility = View.GONE


                //load pdf


            }
            .addOnFailureListener { e ->
                Log.e(TAG, "loadBookFromUrl: Failed to get url due to ${e.message}")
                binding.progressBar.visibility = View.GONE
            }

    }

    private fun loadGitHub() {
        binding.progressBar.visibility = View.VISIBLE
        binding.titleTv.text = getString(R.string.git_hub)
        try {

            val urlToLoad = "https://github.com/ST10029788/UniTalk.git"
            binding.webView.loadUrl(urlToLoad)
            binding.webView.visibility = View.GONE

            android.os.Handler().postDelayed({
                binding.progressBar.visibility = View.GONE
                binding.webView.visibility = View.VISIBLE
            }, 2000)
        } catch (e: Exception) {
            Log.e(TAG, "loadGitHub: ${e.message}")
        }


    }


}