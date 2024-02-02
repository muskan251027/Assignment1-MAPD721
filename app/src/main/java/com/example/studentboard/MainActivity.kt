package com.example.studentboard

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studentboard.data.StudentPreferences
import com.example.studentboard.ui.theme.StudentBoardTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudentBoardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StudentBoard()
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun StudentBoard() {

    val context = LocalContext.current

    val username = remember {
        mutableStateOf(TextFieldValue(""))
    }
    val email = remember {
        mutableStateOf(TextFieldValue(""))
    }
    val id = remember {
        mutableStateOf(TextFieldValue("676"))
    }

    // Get instance of Student preferences to mutate the stored data
    val studentPreferences = StudentPreferences.getInstance(context)

    // Toast message to load the messages during save and clear of data
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var showLoadedData by remember { mutableStateOf(false) }

    fun showAndHideToast() {
        if (toastMessage != null) {
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
            toastMessage = null
        }
    }

    // Main Interface
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            TextField(
                value = username.value,
                onValueChange = { username.value = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = id.value,
                onValueChange = {
                    id.value = it
                },
                label = { Text("ID") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            val studentInfo = studentPreferences.studentInfo.first()
                            username.value = TextFieldValue(studentInfo.username)
                            email.value = TextFieldValue(studentInfo.email)
                            id.value = TextFieldValue(studentInfo.id.toString())

                            // Set the variable to show loaded data
                            if (username.value.text.isNotEmpty() && email.value.text.isNotEmpty() && id.value.text.isNotEmpty()) {
                                showLoadedData = true
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Load")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            studentPreferences.saveStudentInfo(
                                username.value.text,
                                email.value.text,
                                id.value.text.toInt()
                            )
                            toastMessage = "Saved successfully"
                            // Reset fields after saving
                            username.value = TextFieldValue("")
                            email.value = TextFieldValue("")
                            id.value = TextFieldValue("676")

                            withContext(Dispatchers.Main) {
                                showAndHideToast()
                            }
                            showLoadedData = false
                        }

                    },
                    modifier = Modifier.weight(1f),
                    enabled = true
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            studentPreferences.clearStudentInfo()
                            toastMessage = "Removed successfully"
                            // Reset fields after clearing
                            username.value = TextFieldValue("")
                            email.value = TextFieldValue("")
                            id.value = TextFieldValue("676")

                            withContext(Dispatchers.Main) {
                                showAndHideToast()
                            }
                            showLoadedData = false
                        }
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear")
                }
                Spacer(modifier = Modifier.width(8.dp))

            }

            // Show loaded data when Load button is clicked
            if (showLoadedData) {
                LoadedData(username.value.text, email.value.text, id.value.text)
            }

        }

        AboutSection()

    }

}

@Composable
private fun AboutSection() {
    Spacer(modifier = Modifier.height(50.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(imageVector = Icons.Default.Person, contentDescription = null)
        Spacer(modifier = Modifier.width(4.dp))
        Text("Student name: Muskan Aggarwal", color = Color.Black)
    }
    Spacer(modifier = Modifier.height(5.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(imageVector = Icons.Default.Edit, contentDescription = null)
        Spacer(modifier = Modifier.width(4.dp))
        Text("Student ID: 301399676", color = Color.Black)
    }
}

@Composable
fun LoadedData(username: String, email: String, id: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Text("GENERATED DATA",color = Color.Black, fontStyle = FontStyle.Italic)
        Spacer(modifier = Modifier.height(10.dp))
        Text("Username: $username", color = Color.Black, fontStyle = FontStyle.Italic)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Email: $email", color = Color.Black, fontStyle = FontStyle.Italic)
        Spacer(modifier = Modifier.height(4.dp))
        Text("ID: $id", color = Color.Black, fontStyle = FontStyle.Italic)
    }
}

@Preview(showBackground = true)
@Composable
fun StudentBoardPreview() {
    StudentBoardTheme {
        StudentBoard()
    }
}