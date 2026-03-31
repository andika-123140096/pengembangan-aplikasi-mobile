package com.example.demop4app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.demop4app.viewmodel.TodoViewModel

@Composable
fun AddNoteScreen(
    viewModel: TodoViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text("Back", style = MaterialTheme.typography.labelLarge)
            }

            Text("Add New Note", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = uiState.inputText,
                onValueChange = { viewModel.onInputChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Make it larger/expand
                label = { Text("Note Content") },
                placeholder = { Text("Type your note here...") },
                minLines = 10 // Ensure it looks like a notepad
            )
            
            Button(
                onClick = {
                    viewModel.addTodo()
                    onBack()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = uiState.inputText.isNotBlank()
            ) {
                Text("Save Note")
            }
        }
    }
}
