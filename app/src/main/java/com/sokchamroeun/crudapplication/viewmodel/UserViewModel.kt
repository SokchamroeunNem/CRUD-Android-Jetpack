package com.sokchamroeun.crudapplication.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sokchamroeun.crudapplication.data.User
import com.sokchamroeun.crudapplication.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    val allUsers: StateFlow<List<User>> = repository.getAllUsers().stateIn(
        scope = viewModelScope, started = SharingStarted.Lazily, initialValue = emptyList()
    )

    var registrationState by mutableStateOf(CreateUserFormState())
        private set

    fun onEvent(event: CreateUserFormEvent) {
        when (event) {

            is CreateUserFormEvent.NameChanged -> {
                val error = validateName(event.name)
                registrationState = registrationState.copy(name = event.name, nameError = error)
            }

            is CreateUserFormEvent.LastNameChanged -> {
                val error = validateLastName(event.lastName)
                registrationState =
                    registrationState.copy(lastName = event.lastName, lastNameError = error)
            }

            is CreateUserFormEvent.EmailChanged -> {
                val error = validateEmail(event.email)
                registrationState = registrationState.copy(email = event.email, emailError = error)
            }

            is CreateUserFormEvent.Submit -> {
                if (event.userId == null) {
                    submitData()
                } else {
                    updateUser(event.userId)
                }
            }

        }
    }

    fun clearFormData() {
        registrationState = CreateUserFormState()
    }

    fun validateName(name: String): String? {
        return if (name.isBlank()) "Name cannot be empty" else null
    }

    fun validateLastName(name: String): String? {
        return if (name.isBlank()) "LastName cannot be empty" else null
    }

    fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email cannot be empty"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email address"
            else -> null
        }
    }

    private fun validateForm(): Boolean {
        val nameError = validateName(registrationState.name)
        val lastNameError = validateLastName(registrationState.lastName)
        val emailError = validateEmail(registrationState.email)

        registrationState = registrationState.copy(
            nameError = nameError, lastNameError = lastNameError, emailError = emailError
        )

        return nameError == null && lastNameError == null && emailError == null
    }

    private fun submitData() {
        if (validateForm()) {
            val user = User(
                firstname = registrationState.name,
                lastname = registrationState.lastName,
                email = registrationState.email
            )
            addUser(user)

            // Clear form data after successful submission
            clearFormData()
        }
    }

    private fun updateUser(userId: Int) {
        if (validateForm()) {
            val user = User(
                id = userId,
                firstname = registrationState.name,
                lastname = registrationState.lastName,
                email = registrationState.email
            )
            update(user)

            // Clear form data after successful update
            clearFormData()
        }
    }

    private fun update(user: User) {
        viewModelScope.launch {
            repository.update(user)
        }
    }

    fun getUserById(userId: Int, onComplete: (User?) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserById(userId)
            onComplete(user)
        }
    }

    private fun addUser(user: User) {
        viewModelScope.launch {
            repository.insert(user)
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            repository.delete(user)
        }
    }

    fun deleteUserById(userId: Int) {
        viewModelScope.launch {
            repository.deleteById(userId)
        }
    }

}

class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class CreateUserFormState(
    var name: String = "",
    val nameError: String? = null,
    var lastName: String = "",
    val lastNameError: String? = null,
    var email: String = "",
    val emailError: String? = null
)

sealed class CreateUserFormEvent {
    data class NameChanged(val name: String) : CreateUserFormEvent()
    data class LastNameChanged(val lastName: String) : CreateUserFormEvent()
    data class EmailChanged(val email: String) : CreateUserFormEvent()

    data class Submit(val userId: Int? = null) : CreateUserFormEvent()
}
