package com.example.collegealertapplication.presentation.screens.alert


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.collegealertapplication.data.local.NotificationDatabase
import com.example.collegealertapplication.data.local.NotificationRepository


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertScreen() {
    val context = LocalContext.current
    val db = NotificationDatabase.getInstance(context)
    val repo = NotificationRepository(db.notificationDao())
    val notifications by repo.getAllNotifications().collectAsState(initial = emptyList())

    Column {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Alerts",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxSize()
    ) {
        items(notifications) { notif ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = notif.title, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = notif.message)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm")
                            .format(java.util.Date(notif.timestamp)),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    } }
}
