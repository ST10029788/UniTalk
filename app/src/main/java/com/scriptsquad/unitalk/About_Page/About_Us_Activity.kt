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

class About_Us_Activity : AppCompatActivity() {


    private lateinit var binding: ActivityAboutUsBinding

    private companion object {
        private const val TAG = "ABOUT_US_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val resumeDetail = "POE"
        val githubDetail = "github"


        binding.resumeRl.setOnClickListener {
            val intent = Intent(this@About_Us_Activity, About_Details_Activity::class.java)
            intent.putExtra("Detail", resumeDetail)
            startActivity(intent)
        }

        binding.githubRl.setOnClickListener {
            val intent = Intent(this@About_Us_Activity, About_Details_Activity::class.java)
            intent.putExtra("Detail", githubDetail)
            startActivity(intent)
        }

        binding.emailiv.setOnClickListener {
            gmailSender()
        }

        binding.emailTv1.setOnClickListener {
            gmailSender()
        }

        binding.whatsAppIv.setOnClickListener {
            openWhatsApp()
        }

        binding.whatsAppTv.setOnClickListener {
            openWhatsApp()
        }


        loadImages()

    }

    private fun gmailSender() {
        val recipient = "scriptsquad@gmail.com"
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$recipient"))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
        intent.putExtra(Intent.EXTRA_TEXT, "Email body")
        startActivity(Intent.createChooser(intent, "Send Email"))
    }

    private fun loadImages() {
        Log.d(TAG, "loadImages: ")

        try {

            Glide.with(this@About_Us_Activity)
                .load(R.drawable.leader)
                .placeholder(R.drawable.ic_person_white)
                .into(binding.personIv)
        } catch (e: Exception) {
            Log.e(TAG, "loadImages:exception: $e")
            Toast.makeText(this@About_Us_Activity, "${e.message}", Toast.LENGTH_SHORT).show()
        }


    }

    private fun openWhatsApp() {

        try {

            val phoneNumber = "123456789"
            
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





