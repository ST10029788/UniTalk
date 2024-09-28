package com.scriptsquad.unitalk.Marketplace.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.scriptsquad.unitalk.Marketplace.adapter.AdapterChats
import com.scriptsquad.unitalk.databinding.FragmentChatsBinding
import com.scriptsquad.unitalk.Marketplace.model.ModelChats


class ChatsFragment : Fragment() {

    companion object {

        private const val TAG = "CHATS_TAG"
    }

    private lateinit var firebaseAuth: FirebaseAuth

    private var myUid = ""

    private lateinit var mContext: Context
    private lateinit var binding: FragmentChatsBinding

    private lateinit var chatArrayList: ArrayList<ModelChats>

    private lateinit var adapterChats: AdapterChats

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        myUid = "${firebaseAuth.uid}"
        Log.d(TAG, "onViewCreated: myUid: $myUid")

        loadChats()

        binding.searchEt.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {



                try {

                    val query = s.toString()
                    Log.d(TAG, "onTextChanged: Search Query: $query ")

                    adapterChats.filter.filter(query)
                }
                catch (e:Exception){
                    Log.e(TAG, "onTextChanged: ",e )
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun loadChats() {

        chatArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Chats")
        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                chatArrayList.clear()

                for (ds in snapshot.children) {

                    val chatKey = "${ds.key}"
                    Log.d(TAG, "onDataChange: chatKey: $chatKey")

                    if (chatKey.contains(myUid)) {
                        Log.d(TAG, "onDataChange: Contains, Add to List")

                        val modelChats = ModelChats()
                        modelChats.chatKey = chatKey

                        chatArrayList.add(modelChats)
                    } else {
                        Log.d(TAG, "onDataChange: Not Contains, Skip")
                    }

                }

                adapterChats = AdapterChats(mContext, chatArrayList)
                binding.chatsRv.adapter = adapterChats


                //sorting with newest message first
                sort()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun sort() {
        //Delay 1 sec before sorting the list

        Handler().postDelayed({
            chatArrayList.sortWith { model1: ModelChats, model2: ModelChats ->
                model2.timestamp.compareTo(model1.timestamp)
            }
            adapterChats.notifyDataSetChanged()
        }, 1000)
    }

}