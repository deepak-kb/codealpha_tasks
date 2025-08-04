package com.example.collegealertapplication.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.collegealertapplication.presentation.screens.admin.AddItemScreen
import com.example.collegealertapplication.presentation.screens.admin.AdminViewModel
import com.example.collegealertapplication.presentation.screens.alert.AlertScreen
import com.example.collegealertapplication.presentation.screens.auth.AuthViewModel
import com.example.collegealertapplication.presentation.screens.auth.login.LoginScreen
import com.example.collegealertapplication.presentation.screens.auth.signup.SignUpScreen
import com.example.collegealertapplication.presentation.screens.home.HomeScreen
import com.example.collegealertapplication.presentation.screens.home.HomeViewModel
import com.example.collegealertapplication.presentation.screens.profile.ProfileScreen
import com.example.collegealertapplication.presentation.screens.welcome.WelcomeScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation(navController: NavHostController) {
    val homeViewModel: HomeViewModel = viewModel()
    val adminViewModel: AdminViewModel = viewModel()

    val authViewModel: AuthViewModel = viewModel()
    val uiState by authViewModel.uiState.collectAsState()
    val user = uiState.loggedUser

    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            authViewModel.fetchUserProfile()
        }
    }
    val isAdmin = currentUser?.email == "deepak@gmail.com"

    // Show the start destination based on user presence
    NavHost(
        navController = navController,
        startDestination = if (currentUser == null) "welcome" else "home"
    ) {
        // Welcome or Onboarding screen
        composable("welcome") {
            WelcomeScreen(
                onNavigateToSignUp = { navController.navigate("signup") },
                onNavigateToLogin   = { navController.navigate("login") }
            )
        }
        composable("signup") {
            SignUpScreen(
                onNavigateToLogin = { navController.navigate("login")  },
                onSignUpSuccess   = { navController.navigate("login") { popUpTo("welcome") { inclusive = true } } },
                onNavigateBack    = navController::navigateUp,
                viewModel = authViewModel
            )
        }

        composable("login") {
            LoginScreen(
                onNavigateToSignUp = { navController.navigate("signup") },
                onLoginSuccess     = { navController.navigate("home") { popUpTo("welcome") { inclusive = true } } },
                onNavigateBack     = navController::navigateUp,
                viewModel = authViewModel,
            )
        }
        composable("home") {
            HomeScreen(
                navController = navController,
                homeViewModel = homeViewModel,
                adminViewModel = adminViewModel,
                authViewModel=authViewModel,
                isAdmin = isAdmin,
                user = user      // Pass the current logged-in User
            )
        }
        composable("alerts") { AlertScreen() }

        composable("add_item") {
            AddItemScreen(
                navController = navController,
                adminViewModel = adminViewModel
            )
        }

        composable("profile") {
            if (user != null) {
                ProfileScreen(
                    user = user,
                    authViewModel=authViewModel,
                    onNavigateToAlerts = { navController.navigate("alerts") },
                    onNavigateToWelcome = {
                        // Optionally clear navigation stack
                        navController.navigate("welcome") { popUpTo(0) }
                    },
//                    onNavigateToHome = { navController.navigate("home") },
//                    onNavigateToEvents = { navController.navigate("events") },
                    onNavigateToProfile = { /* Already on Profile, do nothing */ },
                    onNavigateToHelp = { navController.navigate("help") }
                )
            } else {
                // Loading or error UI
                CircularProgressIndicator(Modifier.padding(32.dp))
            }
        }


    }
}
