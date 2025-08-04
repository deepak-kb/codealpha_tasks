package com.example.collegealertapplication.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegealertapplication.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val fullName: String = "",
    val mobileNumber: String = "",
    val email: String = "",
    val dateOfBirth: String = "",
    val password: String = "",
    val collegeAffiliation: String = "",
    val emailOrMobile: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false,
    val signUpSuccess: Boolean = false,
    val loggedUser: User? = null
)

class AuthViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun updateFullName(fullName: String) {
        _uiState.value = _uiState.value.copy(fullName = fullName)
    }
    fun updateMobileNumber(mobile: String) {
        _uiState.value = _uiState.value.copy(mobileNumber = mobile)
    }
    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }
    fun updateDateOfBirth(dob: String) {
        _uiState.value = _uiState.value.copy(dateOfBirth = dob)
    }
    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }
    fun updateCollegeAffiliation(college: String) {
        _uiState.value = _uiState.value.copy(collegeAffiliation = college)
    }
    fun updateEmailOrMobile(value: String) {
        _uiState.value = _uiState.value.copy(emailOrMobile = value)
    }

    fun reset() {
        _uiState.value = AuthUiState()
    }

    fun fetchUserProfile() {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val uid = currentUser.uid
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val user = User(
                    uid = uid,
                    email = doc.getString("email") ?: "",
                    fullName = doc.getString("fullName") ?: "",
                    mobileNumber = doc.getString("mobileNumber") ?: "",
                    dateOfBirth = doc.getString("dateOfBirth") ?: "",
                    collegeAffiliation = doc.getString("collegeAffiliation") ?: "",
                    createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                )
                _uiState.value = _uiState.value.copy(loggedUser = user)
            }
            .addOnFailureListener { e ->
                // Optionally handle error, e.g. show error message
            }
    }

    fun signUp() {
        val state = _uiState.value
        if (state.fullName.isBlank() ||
            state.mobileNumber.isBlank() ||
            state.email.isBlank() ||
            state.dateOfBirth.isBlank() ||
            state.collegeAffiliation.isBlank() ||
            state.password.isBlank()
        ) {
            _uiState.value = state.copy(errorMessage = "All fields are required")
            return
        }
        _uiState.value = state.copy(isLoading = true, errorMessage = null, signUpSuccess = false)
        auth.createUserWithEmailAndPassword(state.email, state.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser!!.uid
                    val timestamp = System.currentTimeMillis()
                    // Save user profile data to Firestore (with uid as doc id)
                    val userProfile = mapOf(
                        "uid" to uid,
                        "fullName" to state.fullName,
                        "mobileNumber" to state.mobileNumber,
                        "email" to state.email,
                        "dateOfBirth" to state.dateOfBirth,
                        "collegeAffiliation" to state.collegeAffiliation,
                        "createdAt" to timestamp
                    )
                    db.collection("users").document(uid).set(userProfile)
                        .addOnSuccessListener {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                signUpSuccess = true,
                                errorMessage = null,
                                loggedUser = User(
                                    uid = uid,
                                    fullName = state.fullName,
                                    email = state.email,
                                    mobileNumber = state.mobileNumber,
                                    dateOfBirth = state.dateOfBirth,
                                    collegeAffiliation = state.collegeAffiliation,
                                    createdAt = timestamp
                                )
                            )
                        }
                        .addOnFailureListener { e ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = e.localizedMessage ?: "Failed saving profile"
                            )
                        }
                } else {
                    _uiState.value = state.copy(
                        isLoading = false,
                        errorMessage = task.exception?.localizedMessage ?: "Sign up failed"
                    )
                }
            }
    }

    fun login() {
        val state = _uiState.value
        if (state.emailOrMobile.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Email and password are required")
            return
        }
        _uiState.value = state.copy(isLoading = true, errorMessage = null, loginSuccess = false)
        // Only login by email for Firebase Auth
        auth.signInWithEmailAndPassword(state.emailOrMobile, state.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        db.collection("users").document(uid).get()
                            .addOnSuccessListener { doc ->
                                val user = User(
                                    uid = uid,
                                    email = doc.getString("email") ?: "",
                                    fullName = doc.getString("fullName") ?: "",
                                    mobileNumber = doc.getString("mobileNumber") ?: "",
                                    dateOfBirth = doc.getString("dateOfBirth") ?: "",
                                    collegeAffiliation = doc.getString("collegeAffiliation") ?: "",
                                    createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                                )
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    loginSuccess = true,
                                    loggedUser = user
                                )
                            }
                            .addOnFailureListener { e ->
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    errorMessage = e.localizedMessage ?: "Failed loading user profile"
                                )
                            }
                    } else {
                        _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "User not found")
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = task.exception?.localizedMessage ?: "Login failed"
                    )
                }
            }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
