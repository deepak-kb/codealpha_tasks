package com.example.collegealertapplication.data

data class ItemModel(
    val id: String = "",
    val title: String = "",
    val time: String = "",
    val date: String = "",
    val location: String = "",
    val description: String = "",
    val imageUrl: String = ""
)

typealias CourseItem = ItemModel
typealias PanelItem = ItemModel
typealias ExaminationItem = ItemModel
typealias NotificationItem = ItemModel
