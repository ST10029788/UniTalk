package com.scriptsquad.unitalk.Ai_Page

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.ai.client.generativeai.GenerativeModel
import com.scriptsquad.unitalk.R
import com.scriptsquad.unitalk.databinding.ActivityAiactivityBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class AI_Activity : AppCompatActivity() {

    private lateinit var binding: ActivityAiactivityBinding
    private val promptResponseList = mutableListOf<Ai_Prompt_Response_Pair>()
    private lateinit var adapter: Ai_Prompt_Response_Adapter

    private companion object {
        private const val TAG = "AI_TAG"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAiactivityBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        showsplash()


        binding.progressBar.visibility = View.GONE

        setupRecyclerView()

        binding.imageBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }





        binding.submitBtn.setOnClickListener {

            Log.d(TAG, "onCreate: btnClicked")

            val prompt = binding.promptEt.text.toString()!!

            Log.d(TAG, "onCreate: Prompt: $prompt")

            generateContentWithListeners(prompt,
                object : ProgressListener {  // Anonymous object for ProgressListener
                    override fun onProgressUpdate(progress: Float) {
                        binding.progressBar.visibility = View.GONE
                        binding.progressBar.progress = (progress * 100).toInt()
                        Log.d(TAG, "onProgressUpdate: progress: $progress")
                    }
                },

                object : SuccessListener {
                    override fun onSuccess(response: String) {
                        Log.d(TAG, "onSuccess: $response")
                        binding.progressBar.visibility = View.GONE
                        // Update to add to list and notify adapter

                        val pair = Ai_Prompt_Response_Pair(prompt, response)
                        promptResponseList.add(pair)
                        val insertPosition = promptResponseList.size - 1
                        adapter.notifyItemInserted(insertPosition)

                        // Scroll to the new item's position
                        binding.promptResponseRecyclerView.smoothScrollToPosition(insertPosition)

                        // Clear the EditText for the next input
                        binding.promptEt.setText("")


                    }
                })


        }


    }

    val generativeModel = GenerativeModel(
        // For text-only input, use the gemini-pro model
        modelName = "gemini-pro",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above) getString(R.string.gimni_api_key)
        apiKey = "AIzaSyBmupJEwQoFtb9xtQOJXhM064h5n7x2-S0"
    )

    // Define interfaces for progress and success callbacks
    interface ProgressListener {
        fun onProgressUpdate(progress: Float)
    }


    interface SuccessListener {
        fun onSuccess(response: String)
    }

    private fun setupRecyclerView() {
        adapter = Ai_Prompt_Response_Adapter(promptResponseList)
        binding.promptResponseRecyclerView.adapter = adapter
        binding.promptResponseRecyclerView.layoutManager = LinearLayoutManager(this)

    }
    data class RequestBody(val prompt: String)



    private fun generateContentWithListeners(
        prompt: String,
        progressListener: ProgressListener?,
        successListener: SuccessListener?
    ) = CoroutineScope(Dispatchers.IO).launch {
        try {
            // Create the request body using Gson
            val requestBody = RequestBody(prompt) // Create the request body with the prompt

            // Make the API call
            val response = RetrofitClient.apiService.generateResponse(requestBody)

            Log.d(TAG, "generateContentWithListeners: $prompt")
            withContext(Dispatchers.Main) {
                // Assuming your response has a property named 'response'
                successListener?.onSuccess(response.response) // Access the correct property
                progressListener?.onProgressUpdate(1.0f) // Update progress on success as well
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "generateContentWithListeners: ${e.message}")
            withContext(Dispatchers.Main) {
                MotionToast.createToast(
                    this@AI_Activity,
                    "Failed",
                    "Failed to generate content due to ${e.message}",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this@AI_Activity, R.font.baloo_bhai)
                )
            }
        }
    }


    private fun showsplash() {
        val dialog =
            Dialog(this@AI_Activity, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.ai_splash)
        dialog.setCancelable(true)
        dialog.show()
        val handler = Handler()
        val runnable = Runnable {
            dialog.dismiss()

        }
        handler.postDelayed(runnable, 5000)
    }


}