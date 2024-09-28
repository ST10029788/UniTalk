package com.scriptsquad.unitalk.Notes.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.scriptsquad.unitalk.Notes.adapter.AdapterPdfUser
import com.scriptsquad.unitalk.Notes.model.ModelBookPdf
import com.scriptsquad.unitalk.databinding.FragmentBookUserBinding


class BookUserFragment : Fragment() {

    private lateinit var binding: FragmentBookUserBinding
    private lateinit var mContext: Context

    private var categoryId: String = ""
    private var category: String = ""
    private var uid: String = ""

    private lateinit var pdfArrayList: ArrayList<ModelBookPdf>
    private lateinit var adapterPdfUser: AdapterPdfUser

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object {
        private const val TAG = "BOOK_USER_FRAGMENT_TAG"

        fun newInstance(
            categoryId: String,
            category: String,
            uid: String
        ): BookUserFragment {
            val fragment = BookUserFragment()
            val args = Bundle().apply {
                putString("categoryId", categoryId)
                putString("category", category)
                putString("uid", uid)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryId = it.getString("categoryId") ?: ""
            category = it.getString("category") ?: ""
            uid = it.getString("uid") ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookUserBinding.inflate(inflater, container, false)

        Log.d(TAG, "onCreateView: Category: $category")

        when (category) {
            "All" -> loadAllBooks()
            "Most Viewed" -> loadMostViewedDownloadedBooks("viewCount")
            "Most Download" -> loadMostViewedDownloadedBooks("downloadsCount")
            else -> loadCategorizedBooks()
        }

        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    adapterPdfUser.filter.filter(s)
                } catch (e: Exception) {
                    Log.d(TAG, "onTextChanged: Search Exception: ${e.message}")
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return binding.root
    }

    private fun loadAllBooks() {
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pdfArrayList.clear()
                for (ds in snapshot.children) {
                    val model = ds.getValue(ModelBookPdf::class.java)
                    model?.let { pdfArrayList.add(it) } // Add model only if not null
                }
                adapterPdfUser = AdapterPdfUser(mContext, pdfArrayList)
                binding.booksRv.adapter = adapterPdfUser
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error loading Notes:", error.toException())
            }
        })
    }

    private fun loadMostViewedDownloadedBooks(orderBy: String) {
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.orderByChild(orderBy)
            .limitToLast(10)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pdfArrayList.clear()
                    for (ds in snapshot.children) {
                        val model = ds.getValue(ModelBookPdf::class.java)
                        model?.let { pdfArrayList.add(it) } // Add model only if not null
                    }
                    adapterPdfUser = AdapterPdfUser(mContext, pdfArrayList)
                    binding.booksRv.adapter = adapterPdfUser
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error loading Notes:", error.toException())
                }
            })
    }

    private fun loadCategorizedBooks() {
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.orderByChild("categoryId").equalTo(categoryId)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    pdfArrayList.clear()

                    for (ds in snapshot.children) {
                        //get data
                        val model = ds.getValue(ModelBookPdf::class.java)

                        //add to list
                        pdfArrayList.add(model!!)
                    }

                    adapterPdfUser = AdapterPdfUser(mContext, pdfArrayList)

                    //set adapter to recyclerView

                    binding.booksRv.adapter = adapterPdfUser
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }


}