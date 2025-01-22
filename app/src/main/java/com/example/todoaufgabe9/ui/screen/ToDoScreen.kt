package com.example.todoaufgabe9.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todoaufgabe9.database.ToDoController
import com.example.todoaufgabe9.model.ToDo

/**
 * Hauptbildschirm der ToDo-App.
 *
 * Diese Composable-Funktion verwaltet die Anzeige der aktiven und erledigten ToDos sowie die
 * Erstellung, Bearbeitung und Löschung von ToDos.
 *
 * @param context Der Kontext der Anwendung, der für den Zugriff auf die Datenbank benötigt wird.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoScreen(context: Context) {
    val todoController = ToDoController(context)

    // State-Variablen für aktive und erledigte ToDos
    var activeTodos by remember { mutableStateOf(todoController.getAllToDos().filter { !it.state }) }
    var completedTodos by remember { mutableStateOf(todoController.getAllToDos().filter { it.state }) }

    // State-Variablen für Dialoge
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var todoToEdit by remember { mutableStateOf<ToDo?>(null) }

    // State für den aktuell ausgewählten Tab (0 = Aktiv, 1 = Erledigt)
    var selectedTab by remember { mutableStateOf(0) }

    // Hauptgerüst des Bildschirms mit TopAppBar und FloatingActionButton
    Scaffold(
        topBar = { TopAppBar(title = { Text("ToDo App") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add ToDo")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Tab-Leiste für die Auswahl zwischen aktiven und erledigten ToDos
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("Aktive ToDos")
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("Erledigte ToDos")
                }
            }

            // Anzeige der ToDos in einer Liste
            LazyColumn {
                val todos = if (selectedTab == 0) activeTodos else completedTodos
                items(todos) { todo ->
                    ToDoCard(
                        todo = todo,
                        onComplete = {
                            // Markiert ein ToDo als erledigt
                            todoController.updateToDo(todo.copy(state = true))
                            activeTodos = todoController.getAllToDos().filter { !it.state }
                            completedTodos = todoController.getAllToDos().filter { it.state }
                        },
                        onDelete = {
                            // Löscht ein ToDo
                            todoController.deleteToDo(todo.id)
                            activeTodos = todoController.getAllToDos().filter { !it.state }
                            completedTodos = todoController.getAllToDos().filter { it.state }
                        },
                        onEdit = {
                            // Öffnet den Bearbeitungsdialog
                            todoToEdit = it
                            showEditDialog = true
                        }
                    )
                }
            }
        }
    }

    // Dialog für das Hinzufügen eines neuen ToDos
    if (showAddDialog) {
        AddToDoDialog(
            onDismiss = { showAddDialog = false },
            onSave = { newToDo ->
                todoController.insertToDo(newToDo)
                activeTodos = todoController.getAllToDos().filter { !it.state }
                showAddDialog = false
            }
        )
    }

    // Dialog für die Bearbeitung eines bestehenden ToDos
    if (showEditDialog && todoToEdit != null) {
        EditToDoDialog(
            todo = todoToEdit!!,
            onDismiss = { showEditDialog = false },
            onSave = { updatedToDo ->
                todoController.updateToDo(updatedToDo)
                activeTodos = todoController.getAllToDos().filter { !it.state }
                completedTodos = todoController.getAllToDos().filter { it.state }
                showEditDialog = false
            }
        )
    }
}

/**
 * Einzelne Karte für ein ToDo-Element.
 *
 * Zeigt die Details eines ToDos an und bietet Optionen zum Erledigen, Bearbeiten oder Löschen.
 *
 * @param todo Das ToDo-Element, das angezeigt wird.
 * @param onComplete Callback, das aufgerufen wird, wenn das ToDo als erledigt markiert wird.
 * @param onDelete Callback, das aufgerufen wird, wenn das ToDo gelöscht wird.
 * @param onEdit Callback, das aufgerufen wird, um das ToDo zu bearbeiten.
 */
@Composable
fun ToDoCard(todo: ToDo, onComplete: (ToDo) -> Unit, onDelete: (ToDo) -> Unit, onEdit: (ToDo) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(todo.name, style = MaterialTheme.typography.titleLarge)
            Text("Priority: ${todo.priority}", style = MaterialTheme.typography.bodyMedium)
            Text("Deadline: ${todo.enddate}", style = MaterialTheme.typography.bodySmall)
            Text(todo.description, style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                if (!todo.state) {
                    Button(onClick = { onComplete(todo) }) {
                        Text("Erledigen")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Button(onClick = { onEdit(todo) }) {
                    Text("Bearbeiten")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onDelete(todo) }) {
                    Text("Löschen")
                }
            }
        }
    }
}

/**
 * Dialog zum Hinzufügen eines neuen ToDos.
 *
 * @param onDismiss Callback, wenn der Dialog geschlossen wird.
 * @param onSave Callback, wenn das neue ToDo gespeichert wird.
 */
@Composable
fun AddToDoDialog(onDismiss: () -> Unit, onSave: (ToDo) -> Unit) {
    var name by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("") }
    var enddate by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isFormValid by remember { mutableStateOf(false) }

    // Überprüfung der Formularfelder
    LaunchedEffect(name, priority, enddate, description) {
        isFormValid = name.isNotBlank() &&
                priority.toIntOrNull() != null &&
                enddate.isNotBlank() &&
                description.isNotBlank()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Neues ToDo erstellen") },
        text = {
            Column {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = priority, onValueChange = { priority = it }, label = { Text("Priorität (Zahl)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = enddate, onValueChange = { enddate = it }, label = { Text("Deadline (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = description, onValueChange = { description = it }, label = { Text("Beschreibung") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                val newToDo = ToDo(0, name, priority.toInt(), enddate, description, false)
                onSave(newToDo)
            }, enabled = isFormValid) {
                Text("Speichern")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}

/**
 * Dialog zum Bearbeiten eines bestehenden ToDos.
 *
 * @param todo Das ToDo, das bearbeitet wird.
 * @param onDismiss Callback, wenn der Dialog geschlossen wird.
 * @param onSave Callback, wenn die Änderungen gespeichert werden.
 */
@Composable
fun EditToDoDialog(todo: ToDo, onDismiss: () -> Unit, onSave: (ToDo) -> Unit) {
    // Ähnlich wie AddToDoDialog, aber mit den Werten des bestehenden ToDos.
    var name by remember { mutableStateOf(todo.name) }
    var priority by remember { mutableStateOf(todo.priority.toString()) }
    var enddate by remember { mutableStateOf(todo.enddate) }
    var description by remember { mutableStateOf(todo.description) }
    var isFormValid by remember { mutableStateOf(false) }

    LaunchedEffect(name, priority, enddate, description) {
        isFormValid = name.isNotBlank() &&
                priority.toIntOrNull() != null &&
                enddate.isNotBlank() &&
                description.isNotBlank()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ToDo bearbeiten") },
        text = {
            Column {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = priority, onValueChange = { priority = it }, label = { Text("Priorität (Zahl)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = enddate, onValueChange = { enddate = it }, label = { Text("Deadline (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = description, onValueChange = { description = it }, label = { Text("Beschreibung") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                val updatedToDo = todo.copy(name = name, priority = priority.toInt(), enddate = enddate, description = description)
                onSave(updatedToDo)
            }, enabled = isFormValid) {
                Text("Speichern")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}
