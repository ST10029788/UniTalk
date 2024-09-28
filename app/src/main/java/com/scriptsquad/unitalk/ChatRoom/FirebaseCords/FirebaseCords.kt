package com.scriptsquad.unitalk.ChatRoom.FirebaseCords

import com.google.firebase.firestore.FirebaseFirestore

class FirebaseCords {

    private val firebaseStore:FirebaseFirestore = FirebaseFirestore.getInstance()


    val  MAIN_CHAT_DATABASE = firebaseStore.collection("CHAT")

}