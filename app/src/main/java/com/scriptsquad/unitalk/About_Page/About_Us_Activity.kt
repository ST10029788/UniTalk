package com.scriptsquad.unitalk.About_Page

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.scriptsquad.unitalk.R
import com.scriptsquad.unitalk.databinding.ActivityAboutUsBinding

// This is an Activity class that inherits from AppCompatActivity
class About_Us_Activity : AppCompatActivity() {

    // Lateinit variable to hold the binding for the activity layout
    private lateinit var binding: ActivityAboutUsBinding

    // Companion object to hold constants
    private companion object {
        private const val TAG = "ABOUT_US_TAG"
    }

    // Override the onCreate method
    override fun onCreate(savedInstanceState: Bundle?) {
        // Inflate the activity layout
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        // Call the superclass onCreate method
        super.onCreate(savedInstanceState)
        // Set the content view to the inflated layout
        setContentView(binding.root)

        // Set a click listener for the back button
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Define resume and GitHub details
        val resumeDetail = "POE"
        val githubDetail = "github"

        // Set a click listener for the resume relative layout
        binding.resumeRl.setOnClickListener {
            // Create an intent to start the About_Details_Activity
            val intent = Intent(this@About_Us_Activity, About_Details_Activity::class.java)
            intent.putExtra("Detail", resumeDetail)
            startActivity(intent)
        }

        // Set a click listener for the GitHub relative layout
        binding.githubRl.setOnClickListener {
            // Create an intent to start the About_Details_Activity
            val intent = Intent(this@About_Us_Activity, About_Details_Activity::class.java)
            intent.putExtra("Detail", githubDetail)
            startActivity(intent)
        }

        // Set a click listener for the email image view
        binding.emailiv.setOnClickListener {
            // Call the gmailSender function
            gmailSender()
        }

        // Set a click listener for the email text view
        binding.emailTv1.setOnClickListener {
            // Call the gmailSender function
            gmailSender()
        }

        // Set a click listener for the WhatsApp image view
        binding.whatsAppIv.setOnClickListener {
            // Call the openWhatsApp function
            openWhatsApp()
        }

        // Set a click listener for the WhatsApp text view
        binding.whatsAppTv.setOnClickListener {
            // Call the openWhatsApp function
            openWhatsApp()
        }

        // Call the loadImages function
        loadImages()

    }

    // Method to send an email using Gmail
    private fun gmailSender() {
        // Define the recipient email address
        val recipient = "scriptsquad@gmail.com"
        // Create an intent to send an email
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$recipient"))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
        intent.putExtra(Intent.EXTRA_TEXT, "Email body")
        // Start the email chooser
        startActivity(Intent.createChooser(intent, "Send Email"))
    }

    // Method to load images using Glide
    private fun loadImages() {
        Log.d(TAG, "loadImages: ")

        try {
            // Load the leader image using Glide
            Glide.with(this@About_Us_Activity)
                .load(R.drawable.leader)
                .placeholder(R.drawable.ic_person_white)
                .into(binding.personIv)
        } catch (e: Exception) {
            Log.e(TAG, "loadImages:exception: $e")
            Toast.makeText(this@About_Us_Activity, "${e.message}", Toast.LENGTH_SHORT).show()
        }


    }

    // Method to open WhatsApp
    private fun openWhatsApp() {

        try {
            // Define the phone number
            val phoneNumber = "123456789"
            // Create an intent to open WhatsApp
            val intent = Intent(Intent.ACTION_VIEW)

            intent.setPackage("com.whatsapp")

            intent.data = Uri.parse("whatsapp://send?phone=" + phoneNumber)

            startActivity(intent)
        }catch (e:Exception){
            Log.e(TAG, "openWhatsApp: $e", )
            Toast.makeText(this@About_Us_Activity, "Failed due to ${e.message}", Toast.LENGTH_SHORT).show()
        }


    }

}





