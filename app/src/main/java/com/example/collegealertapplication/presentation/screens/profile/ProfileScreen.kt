package com.example.collegealertapplication.presentation.screens.profile

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.collegealertapplication.R
import com.example.collegealertapplication.data.User
import com.example.collegealertapplication.presentation.screens.auth.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    user: User,
    authViewModel: AuthViewModel,
    onNavigateToAlerts: () -> Unit,
    onNavigateToWelcome: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToHelp: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(35.dp))
        Text("Profile", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))


        // Circular avatar placeholder (replace with actual image if needed)
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.avatar_profile), // Replace with your avatar/icon
                contentDescription = "Profile Picture",
                modifier = Modifier.size(115.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(user.fullName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Student", style = MaterialTheme.typography.bodyMedium)
        Text(user.collegeAffiliation, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))



        // Settings section
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            Text("Settings", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            ProfileMenuItem("Notifications", R.drawable.ic_notification) {
                onNavigateToAlerts()
            }
            ProfileMenuItem("Privacy", R.drawable.outline_privacy_tip_24) {
                // TODO: Implement privacy navigation/actions
            }
        }

        Spacer(modifier = Modifier.height(24.dp))


        // Support section
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            Text("Support", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            ProfileMenuItem("Help & Support", R.drawable.outline_help_24) {
                onNavigateToHelp()
            }
            ProfileMenuItem("Log Out", R.drawable.outline_login_24) {
                // Log out and navigate to welcome
                FirebaseAuth.getInstance().signOut()
                authViewModel.reset()
                onNavigateToWelcome()
            }
        }
    }

}

@Composable
fun ProfileMenuItem(
    title: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, style = MaterialTheme.typography.bodyLarge)
    }
}

