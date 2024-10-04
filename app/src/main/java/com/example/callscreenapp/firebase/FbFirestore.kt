package com.example.callscreenapp.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.serialization.Serializable

var displaySub = MutableLiveData<Int>()

@Serializable
data class ConfigFS (
    var showSub: Int? = 0
)

class FbFirestore {
    private val documentRef = FirebaseFirestore.getInstance();
    private var listener: ListenerRegistration? = null
    private var configFS: ConfigFS? = null

    fun getConfig (completion : () -> Unit){
            listener = documentRef.collection("Config").document("GGwApBVvTUDLCAsQcM33")
                .addSnapshotListener{ documentSnapshot, error ->
                        if (error != null){
                            completion()
                            return@addSnapshotListener
                        }
                        if (documentSnapshot == null || !documentSnapshot.exists()) {
                            completion()
                            return@addSnapshotListener
                        }
                        configFS = documentSnapshot.toObject(ConfigFS::class.java)
                        configFS?.let {
                            displaySub.postValue(it.showSub ?: 0)
                        }
                    completion()
                    }
        }

    fun stopListener() {
        listener?.remove()
    }

    companion object {
        var Instance = FbFirestore()
    }
}