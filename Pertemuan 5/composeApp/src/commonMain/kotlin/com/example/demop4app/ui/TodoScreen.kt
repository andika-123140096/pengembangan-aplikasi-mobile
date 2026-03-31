package com.example.demop4app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.demop4app.data.model.Todo
import com.example.demop4app.viewmodel.TodoViewModel

@Composable
fun TodoScreen(
    viewModel: TodoViewModel,
    onNoteClick: (Int) -> Unit,
    onAddNoteClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNoteClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        },
        containerColor = Color.Transparent
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "My Todos (${uiState.todos.count { it.isDone }}/${uiState.todos.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.todos, key = { it.id }) { todo ->
                    TodoItemCard(
                        todo = todo,
                        onToggle = { viewModel.toggleTodo(todo.id) },
                        onToggleFavorite = { viewModel.toggleFavorite(todo.id) },
                        onDelete = { viewModel.deleteTodo(todo.id) },
                        onClick = { onNoteClick(todo.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun TodoItemCard(
    todo: Todo,
    onToggle: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = todo.isDone,
                onCheckedChange = { onToggle() }
            )
            Text(
                text = todo.text,
                modifier = Modifier.weight(1f),
                textDecoration = if (todo.isDone) TextDecoration.LineThrough else null
            )
            
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (todo.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (todo.isFavorite) Color.Red else Color.Gray
                )
            }

            IconButton(onClick = onDelete) {
                Text("Hapus", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
