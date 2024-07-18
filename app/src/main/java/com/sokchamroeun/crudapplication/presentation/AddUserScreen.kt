package com.sokchamroeun.crudapplication.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sokchamroeun.crudapplication.presentation.components.ValidatedOutlinedTextField
import com.sokchamroeun.crudapplication.viewmodel.CreateUserFormEvent
import com.sokchamroeun.crudapplication.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserScreen(viewModel: UserViewModel, userId: Int, navController: NavController) {

    val isEditMode = remember { userId != -1 }
    val stateValidation = viewModel.registrationState

    LaunchedEffect(userId) {
        if (isEditMode) {
            viewModel.getUserById(userId) { user ->
                user?.let {
                    stateValidation.name = it.firstname
                    stateValidation.lastName = it.lastname
                    stateValidation.email = it.email
                }
            }
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text(if (isEditMode) "Update User" else "Add User") },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                    if (isEditMode) viewModel.clearFormData()
                }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            })
    }) { innerPadding ->

        Box(modifier = Modifier.padding(innerPadding)) {

            Column(modifier = Modifier.padding(16.dp)) {

                ValidatedOutlinedTextField(
                    value = stateValidation.name,
                    onValueChange = { viewModel.onEvent(CreateUserFormEvent.NameChanged(it)) },
                    label = "Name",
                    validate = { viewModel.validateName(it) },
                    error = stateValidation.nameError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                Spacer(modifier = Modifier.height(16.dp))

                ValidatedOutlinedTextField(
                    value = stateValidation.lastName,
                    onValueChange = { viewModel.onEvent(CreateUserFormEvent.LastNameChanged(it)) },
                    label = "Last Name",
                    validate = { viewModel.validateLastName(it) },
                    error = stateValidation.lastNameError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                Spacer(modifier = Modifier.height(16.dp))

                ValidatedOutlinedTextField(
                    value = stateValidation.email,
                    onValueChange = { viewModel.onEvent(CreateUserFormEvent.EmailChanged(it)) },
                    label = "Email",
                    validate = { viewModel.validateEmail(it) },
                    error = stateValidation.emailError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email, contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (userId == -1) {
                            viewModel.onEvent(CreateUserFormEvent.Submit())
                        } else {
                            viewModel.onEvent(CreateUserFormEvent.Submit(userId))
                        }
                        navController.popBackStack()
                    }, modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isEditMode) "Update User" else "Save User")
                }
            }
        }
    }
}

