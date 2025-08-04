package com.example.collegealertapplication.presentation.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegealertapplication.data.ItemModel
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class AdminViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance().reference

    private val _loadedItems = MutableStateFlow<List<ItemModel>>(emptyList())
    val loadedItems: StateFlow<List<ItemModel>> = _loadedItems

    // 🔹 Load Items from Firebase by Category
    fun loadDataForCategory(category: String) {
        val key = when (category) {
            "Seminar" -> "Seminar"
            "Courses" -> "Courses"
            "Panels" -> "Panels"
            "Examination" -> "Examination"
            else -> "Other Notifications"
        }

        viewModelScope.launch {
            database.child(key).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = mutableListOf<ItemModel>()
                    for (child in snapshot.children) {
                        val item = child.getValue(ItemModel::class.java)
                        item?.let { items.add(it) }
                    }
                    _loadedItems.value = items
                }

                override fun onCancelled(error: DatabaseError) {
                    // You can log or show error
                }
            })
        }
    }

    // 🔹 Add Item to Firebase and send notification
//    fun addItem(category: String, item: ItemModel) {
//        val ref = FirebaseDatabase.getInstance().getReference(category)
//        val id = item.id.ifEmpty { UUID.randomUUID().toString() }
//        val updatedItem = item.copy(id = id)
//
//        viewModelScope.launch {
//            ref.child(id).setValue(updatedItem)
//                .addOnSuccessListener {
//                    sendPushNotification(updatedItem.title, updatedItem.description)
//                }
//        }
//    }
//
//    // 🔹 Send Push Notification via FCM
//    private fun sendPushNotification(title: String, message: String) {
//        val fcmUrl = "https://fcm.googleapis.com/fcm/send"
//        val serverKey = "YOUR_SERVER_KEY_HERE" // ⚠️ Replace with your FCM server key
//
//        val json = JSONObject().apply {
//            put("to", "/topics/all")
//            put("priority", "high")
//            put("notification", JSONObject().apply {
//                put("title", title)
//                put("body", message)
//            })
//        }
//
//        val body = RequestBody.create(
//            "application/json; charset=utf-8".toMediaTypeOrNull(),
//            json.toString()
//        )
//
//        val request = Request.Builder()
//            .url(fcmUrl)
//            .post(body)
//            .addHeader("Authorization", "key=$serverKey")
//            .build()
//
//        OkHttpClient().newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                println("Failed to send notification: ${e.message}")
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                println("Notification sent successfully: ${response.body?.string()}")
//            }
//        })
//    }
}
