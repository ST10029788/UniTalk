package com.scriptsquad.unitalk.Notices_Page.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.scriptsquad.unitalk.Notices_Page.adapter.AdapterNotice
import com.scriptsquad.unitalk.Notices_Page.models.modelNotice
import com.scriptsquad.unitalk.Utilities.Utils
import com.scriptsquad.unitalk.databinding.ActivityNoticeBinding

class Notices_Activity : AppCompatActivity() {

    private lateinit var binding: ActivityNoticeBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var noticeArrayList: ArrayList<modelNotice>

    private lateinit var adapterNotice: AdapterNotice


    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityNoticeBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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

                    adapterNotice.filter.filter(s)

                } catch (e: Exception) {
                    Utils.toast(this@Notices_Activity, "Failed to search due to ${e.message}")
                }
            }

        })


        loadNotices()

    }

    private fun loadNotices() {

        // init arrayList

        noticeArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Notices")

        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                // clearing the list before adding data
                noticeArrayList.clear()

                for (ds in snapshot.children) {
                    //get data as model

                    val model = ds.getValue(modelNotice::class.java)

                    //add to array list

                    noticeArrayList.add(model!!)

                }

                //set adapter
                adapterNotice = AdapterNotice(this@Notices_Activity, noticeArrayList)

                // set adapter to recyclerView
                binding.noticeRv.adapter = adapterNotice

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


    }


}