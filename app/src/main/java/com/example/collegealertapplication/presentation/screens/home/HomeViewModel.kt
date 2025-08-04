package com.example.collegealertapplication.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegealertapplication.data.ItemModel
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance().reference

    private val _seminars = MutableStateFlow<List<ItemModel>>(emptyList())
    val seminars: StateFlow<List<ItemModel>> = _seminars

    private val _courses = MutableStateFlow<List<ItemModel>>(emptyList())
    val courses: StateFlow<List<ItemModel>> = _courses

    private val _panels = MutableStateFlow<List<ItemModel>>(emptyList())
    val panels: StateFlow<List<ItemModel>> = _panels

    private val _examinations = MutableStateFlow<List<ItemModel>>(emptyList())
    val examinations: StateFlow<List<ItemModel>> = _examinations

    private val _notifications = MutableStateFlow<List<ItemModel>>(emptyList())
    val notifications: StateFlow<List<ItemModel>> = _notifications

//    init {
//        loadAllDataFromFirebase()
//    }

    fun loadAllDataFromFirebase() {
    loadCategory("Seminar", _seminars)
    loadCategory("Courses", _courses)
    loadCategory("Panels", _panels)          // ✅ Fixed plural
    loadCategory("Examination", _examinations)
    loadCategory("Notifications", _notifications)  // ✅ Fixed plural
}


    private fun loadCategory(
        categoryName: String,
        stateFlow: MutableStateFlow<List<ItemModel>>
    ) {
        database.child(categoryName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val itemList = mutableListOf<ItemModel>()
                for (child in snapshot.children) {
                    val item = child.getValue(ItemModel::class.java)
                    item?.let { itemList.add(it) }
                }
                viewModelScope.launch {
                    stateFlow.emit(itemList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Optional: Log error
            }
        })
    }

}

