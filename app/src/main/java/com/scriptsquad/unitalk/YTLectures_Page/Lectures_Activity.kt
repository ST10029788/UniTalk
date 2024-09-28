package com.scriptsquad.unitalk.YTLectures_Page

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebViewClient
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.scriptsquad.unitalk.Utilities.Utils
import com.scriptsquad.unitalk.databinding.ActivityLecturesBinding

class Lectures_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityLecturesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLecturesBinding.inflate(layoutInflater)
        setContentView(binding.root)


        @JavascriptInterface
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = WebViewClient()
        binding.webView.visibility = View.GONE

        selectCategory()

        binding.toolbarBackBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


    }

    // Function to load Lectures according to year and department and setting Adapter on Spinner

    private fun selectCategory() {
        val spinner: Spinner = binding.spinnerMain
        val listItem = listOf<String>(
            "--- Select your Category ---",
            "BCAD 1st year",
            "BCAD 2nd year",
            "BCAD 3rd year"


        )

        val arrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            // com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            listItem
        )
//        arrayAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter

        spinner.onItemSelectedListener =
            object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {


                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    when (parent.selectedItemPosition) {
                        1 -> {
                            Utils.toast(this@Lectures_Activity, "BCAD 1st Year is Selected:PlayLists are Not Combined!")
                            val urlToLoad = "https://youtube.com/playlist?list=PL480DYS-b_kdyOS1DwUk302QMo_dptXti&si=1CdWhhb5Cs59cirJ"
                            binding.webView.loadUrl(urlToLoad)
                            binding.webView.visibility = View.VISIBLE
                            binding.spinnerMain.visibility = View.GONE
                        }

                        2 -> {
                            Utils.toast(this@Lectures_Activity, "BCAD 2nd Year is Selected")
                            val urlToLoad = "https://youtu.be/hVFI_2fgR-E?si=z3xLXsrXcLZEtMjm"
                            binding.webView.loadUrl(urlToLoad)
                            binding.webView.visibility = View.VISIBLE
                            binding.spinnerMain.visibility = View.GONE
                        }

                        3->{
                            Utils.toast(this@Lectures_Activity, "BCAD 3rd Year is Selected")
                            val urlToLoad =
                                "https://youtu.be/Y_8vEs_p8E4?si=ZYCTp0D5rilu4X11"
                            binding.webView.loadUrl(urlToLoad)
                            binding.webView.visibility = View.VISIBLE
                            binding.spinnerMain.visibility = View.GONE
                        }

                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                }


            }

    }
}