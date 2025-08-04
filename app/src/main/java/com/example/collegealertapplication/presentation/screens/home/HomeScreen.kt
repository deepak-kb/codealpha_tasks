package com.example.collegealertapplication.presentation.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.collegealertapplication.data.ItemModel
import com.example.collegealertapplication.data.User
import com.example.collegealertapplication.R
import com.example.collegealertapplication.presentation.screens.admin.AdminScreen
import com.example.collegealertapplication.presentation.screens.admin.AdminViewModel
import com.example.collegealertapplication.presentation.screens.alert.AlertScreen
import com.example.collegealertapplication.presentation.screens.auth.AuthUiState
import com.example.collegealertapplication.presentation.screens.auth.AuthViewModel
import com.example.collegealertapplication.presentation.screens.bottombar.BottomBar
import com.example.collegealertapplication.presentation.screens.profile.ProfileScreen

@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    adminViewModel: AdminViewModel,
    isAdmin: Boolean,
    user: User?
) {

    var selectedIndex by remember { mutableStateOf(0) }
    Scaffold(
        bottomBar = {
            BottomBar(
                selectedIndex = selectedIndex,
                onItemSelected = { selectedIndex = it },
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            when (selectedIndex) {
                0 -> HomeContent(navController, homeViewModel, user)
                1 -> AlertScreen()
                2 -> AdminScreen(navController, adminViewModel, isAdmin)
                3 -> ProfileScreen(
                    user = user ?: User(),
                    authViewModel = authViewModel,
                    onNavigateToAlerts = { navController.navigate("alerts") },
                    onNavigateToWelcome = { navController.navigate("welcome") { popUpTo(0) } },
                    onNavigateToProfile = { },
                    onNavigateToHelp = { }
                )
            }
        }
    }


}

@Composable
fun HomeContent(
    navController: NavController,
    homeViewModel: HomeViewModel,
    user: User?
) {
    val seminarList by homeViewModel.seminars.collectAsState()
    val courseList by homeViewModel.courses.collectAsState()
    val panelList by homeViewModel.panels.collectAsState()
    val examList by homeViewModel.examinations.collectAsState()
    val notificationList by homeViewModel.notifications.collectAsState()

    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        homeViewModel.loadAllDataFromFirebase()
    }

    Column(
        modifier = Modifier
            .padding(8.dp)
            .verticalScroll(scrollState)
    ) {

        if (user?.fullName?.isNotBlank() == true) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Welcome back, ${user.fullName}",
//                    style = MaterialTheme.typography.headlineSmall,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Image(
                    painter = painterResource(id = R.drawable.avatar_profile),
                    contentDescription = null,
                    modifier = Modifier
                        .size(42.dp)
                        .clickable { navController.navigate("profile") }
                )
            }
        } else {
            Text(
                text = "Welcome back, there!",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Section(title = "Seminar", items = seminarList)
        Section(title = "Courses", items = courseList)
        Section(title = "Panels", items = panelList)
        Section(title = "Examination", items = examList)
        Section(title = "Other Notifications", items = notificationList)
    }
}

@Composable
fun Section(title: String, items: List<ItemModel>) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow {
            items(items) { item ->
                ItemCard(item)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ItemCard(item: ItemModel) {
    Card(
        modifier = Modifier
            .size(width = 180.dp, height = 220.dp)
            .padding(end = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column() {
            Image(
                painter = rememberAsyncImagePainter(item.imageUrl),
                contentDescription = item.title,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    item.title,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
                if (item.time.isNotEmpty()) Text(
                    item.time,
                    style = MaterialTheme.typography.bodySmall
                )
                if (item.date.isNotEmpty()) Text(
                    item.date, maxLines = 2,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}


