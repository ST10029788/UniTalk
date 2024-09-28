package com.scriptsquad.unitalk.Notices_Page.activities

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
import com.scriptsquad.unitalk.Notices_Page.adapter.AdapterAdminNotice
import com.scriptsquad.unitalk.Notices_Page.models.modelNotice
import com.scriptsquad.unitalk.R
import com.scriptsquad.unitalk.Utilities.Utils
import com.scriptsquad.unitalk.databinding.ActivityNoticeDetailAdminBinding

class Admin_Notice_Details_Activity : AppCompatActivity() {

    private lateinit var binding: ActivityNoticeDetailAdminBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var noticeAdminArrayList: ArrayList<modelNotice>

    private lateinit var adapterAdminNotice: AdapterAdminNotice
    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityNoticeDetailAdminBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()


        binding.toolbarBackbtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }



        binding.searchEt.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {

                    adapterAdminNotice.filter.filter(s)

                } catch (e: Exception) {
                    Utils.toast(
                        this@Admin_Notice_Details_Activity,
                        "Failed to search due to ${e.message}"
                    )
                }
            }

        })

        loadNotices()

    }

    private fun loadNotices() {

        // init arrayList

        noticeAdminArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Notices")

        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                // clearing the list before adding data
                noticeAdminArrayList.clear()

                for (ds in snapshot.children) {
                    //get data as model

                    val model = ds.getValue(modelNotice::class.java)

                    //add to array list

                    noticeAdminArrayList.add(model!!)

                }

                //set adapter
                adapterAdminNotice =
                    AdapterAdminNotice(this@Admin_Notice_Details_Activity, noticeAdminArrayList)

                // set adapter to recyclerView
                binding.noticeRv.adapter = adapterAdminNotice

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


    }

}