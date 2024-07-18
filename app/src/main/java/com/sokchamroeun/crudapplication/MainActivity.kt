package com.sokchamroeun.crudapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.sokchamroeun.crudapplication.data.UserDatabase
import com.sokchamroeun.crudapplication.presentation.MainScreen
import com.sokchamroeun.crudapplication.repository.UserRepository
import com.sokchamroeun.crudapplication.ui.theme.CRUDApplicationTheme
import com.sokchamroeun.crudapplication.viewmodel.UserViewModel
import com.sokchamroeun.crudapplication.viewmodel.UserViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var userViewModel: UserViewModel

    private val database by lazy {
        Room.databaseBuilder(
            applicationContext, UserDatabase::class.java, "app_db"
        ).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userRepository = UserRepository(database.userDao())
        val factory = UserViewModelFactory(userRepository)
        userViewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)

        setContent {
            CRUDApplicationTheme {
                MainScreen(userViewModel)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CRUDApplicationTheme {
        Greeting("Android")
    }
}


