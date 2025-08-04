package com.example.collegealertapplication.presentation.screens.admin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavController
import com.example.collegealertapplication.data.ItemModel
import com.google.firebase.database.FirebaseDatabase

@Composable
fun AdminScreen(navController: NavController,viewModel: AdminViewModel,isAdmin:Boolean){

    LaunchedEffect(Unit) {
        viewModel.loadDataForCategory("Seminar")
    }

    if (!isAdmin) {
        Text("Only Admin Can Access this Screen", modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Center)

    }else{
        AddItemScreen(navController,viewModel)
    }
}

@Composable
fun AddItemScreen(navController: NavController, adminViewModel: AdminViewModel) {
    val context = LocalContext.current

    // Create notification channel once
    LaunchedEffect(Unit) {
        createNotificationChannel(context)
    }

    val categories = listOf("Seminar", "Courses", "Panels", "Examination", "Other Notifications")
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(categories.first()) }

    var id by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text(
            text = "Admin Panel",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text("Select Category", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // Modern dropdown (no button)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { expanded = true }
                .padding(horizontal = 16.dp),

            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedCategory,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) androidx.compose.material.icons.Icons.Filled.KeyboardArrowUp
                    else androidx.compose.material.icons.Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.98f)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            selectedCategory = category
                            expanded = false
                        }
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = id, onValueChange = { id = it }, label = { Text("ID") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))


        Button(
            modifier = Modifier.fillMaxWidth().height(48.dp),
            onClick = {
            val item = ItemModel(
                id = id,
                title = title,
                date = date,
                time = time,
                location = location,
                description = description,
                imageUrl = imageUrl
            )

            val dbRef = FirebaseDatabase.getInstance().getReference(selectedCategory)
            dbRef.child(id.ifBlank { dbRef.push().key ?: "item" }).setValue(item)
                .addOnSuccessListener   {
                    Toast.makeText(context, "Data Added", Toast.LENGTH_SHORT).show()
                    showLocalNotification(context, title, "New update in $selectedCategory")
                    navController.popBackStack()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to add data", Toast.LENGTH_SHORT).show()
                }

        }) {
            Text("Submit")
        }
    }
}

// Notification channel (Android 8+)
fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Updates"
        val descriptionText = "Notifications about new items"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("local_alerts", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

// Show local notification
@androidx.annotation.RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS)
fun showLocalNotification(context: Context, title: String, message: String) {
    val builder = NotificationCompat.Builder(context, "local_alerts")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(context)) {
        notify(System.currentTimeMillis().toInt(), builder.build())
    }
}