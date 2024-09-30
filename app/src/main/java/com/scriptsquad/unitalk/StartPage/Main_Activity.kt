package com.scriptsquad.unitalk.StartPage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.scriptsquad.unitalk.Marketplace.activities.Create_Ad_Activity
import com.scriptsquad.unitalk.Marketplace.fragments.HomeFragment
import com.scriptsquad.unitalk.R
import com.scriptsquad.unitalk.Utilities.Utils
import com.scriptsquad.unitalk.databinding.ActivityMainBinding
import com.scriptsquad.unitalk.Account.AccountFragment
import com.scriptsquad.unitalk.Marketplace.fragments.ChatsFragment
import com.scriptsquad.unitalk.Marketplace.fragments.MyAdsFragment
//method used from YouTube
//https://youtu.be/DWIGAkYkpg8?si=um9GgnsGWc9G7KAB
//channel: Android Knowledge

class Main_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser == null) {
            startLogInOption()
        }
        setContentView(binding.root)

        binding.toolbarBackBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        showHomeFragment()
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    showHomeFragment()
                    true
                }

                R.id.menu_chats -> {
                    if (firebaseAuth.currentUser == null) {
                        Utils.toast(this@Main_Activity, "Log_In_Screen Required")
                        startLogInOption()
                        false
                    } else {
                        showChatFragment()
                        true
                    }

                }

                R.id.menu_account -> {

                    if (firebaseAuth.currentUser == null) {
                        Utils.toast(this@Main_Activity, "Log_In_Screen Required")
                        startLogInOption()
                        false
                    } else {
                        showAccountFragment()
                        true
                    }


                }

                R.id.menu_myAds -> {
                    if (firebaseAuth.currentUser == null) {
                        Utils.toast(this@Main_Activity, "Log_In_Screen Required")
                        startLogInOption()
                        false
                    } else {
                        showMyAdsFragment()
                        true
                    }


                }

                R.id.menu_sell -> {
                    if (firebaseAuth.currentUser == null) {
                        Utils.toast(this@Main_Activity, "Log_In_Screen Required")
                        startLogInOption()
                        false
                    } else {

                        true
                    }


                }

                else -> {
                    false
                }

            }
        }
        binding.sellFab.setOnClickListener {
            val intent = Intent(this, Create_Ad_Activity::class.java)
            intent.putExtra("isEditMode", false)
            startActivity(intent)
        }

    }


    private fun showHomeFragment() {
        binding.toolbarTitleTv.text = getString(R.string.home)
        val fragment = HomeFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, fragment, "HomeFragment")
        fragmentTransaction.commit()
    }

    private fun showChatFragment() {
        binding.toolbarTitleTv.text = getString(R.string.chat)
        val fragment = ChatsFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, fragment, "ChatFragment")
        fragmentTransaction.commit()
    }
    //method used from YouTube
    //https://youtu.be/67Xmgqe-wLU?si=sgBeLP6mUaXJxbj7
    //channel: Coder
    private fun showMyAdsFragment() {
        binding.toolbarTitleTv.text = getString(R.string.my_ads)
        val fragment = MyAdsFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, fragment, "MyAdsFragment")
        fragmentTransaction.commit()
    }

    private fun showAccountFragment() {
        binding.toolbarTitleTv.text = getString(R.string.account)
        val fragment = AccountFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, fragment, "AccountFragment")
        fragmentTransaction.commit()
    }

    private fun startLogInOption() {
        val intent = Intent(this@Main_Activity, Log_In_Screen::class.java)
        startActivity(intent)
    }

}




