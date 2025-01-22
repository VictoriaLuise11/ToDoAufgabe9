package com.example.todoaufgabe9.database

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.todoaufgabe9.model.ToDo

/**
 * Controller-Klasse zur Verwaltung von CRUD-Operationen für die `ToDo`-Datenbank.
 *
 * Diese Klasse stellt Methoden bereit, um ToDos aus der Datenbank zu lesen, hinzuzufügen,
 * zu aktualisieren und zu löschen.
 *
 * @param context Der Anwendungskontext.
 */
class ToDoController(context: Context) {
    private val dbHelper = ToDoDbHelper(context)

    /**
     * Ruft alle ToDos aus der Datenbank ab.
     *
     * @return Eine Liste von `ToDo`-Objekten, die in der Datenbank gespeichert sind.
     */
    fun getAllToDos(): List<ToDo> {
        val db = dbHelper.readableDatabase
        val todos = mutableListOf<ToDo>()
        val cursor = db.rawQuery("SELECT * FROM ToDo", null)
        try {
            Log.d("ToDoController", "Query executed: SELECT * FROM ToDo")
            if (cursor.moveToFirst()) {
                do {
                    val todo = ToDo(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        priority = cursor.getInt(cursor.getColumnIndexOrThrow("priority")),
                        enddate = cursor.getString(cursor.getColumnIndexOrThrow("enddate")),
                        description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        state = cursor.getInt(cursor.getColumnIndexOrThrow("state")) == 1
                    )
                    todos.add(todo)
                    Log.d("ToDoController", "Loaded ToDo: $todo")
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e("ToDoController", "Error fetching ToDos", e)
        } finally {
            cursor.close()
            db.close()
        }
        Log.d("ToDoController", "Total ToDos loaded: ${todos.size}")
        return todos
    }

    /**
     * Fügt ein neues ToDo in die Datenbank ein.
     *
     * @param todo Das `ToDo`-Objekt, das gespeichert werden soll.
     * @return `true`, wenn das Einfügen erfolgreich war, andernfalls `false`.
     */
    fun insertToDo(todo: ToDo): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            val values = ContentValues().apply {
                put("name", todo.name)
                put("priority", todo.priority)
                put("enddate", todo.enddate)
                put("description", todo.description)
                put("state", if (todo.state) 1 else 0) // BOOLEAN zu INTEGER
            }
            val result = db.insert("ToDo", null, values)
            Log.d("ToDoController", "Inserted ToDo: $todo")
            result != -1L
        } catch (e: Exception) {
            Log.e("ToDoController", "Error inserting ToDo", e)
            false
        } finally {
            db.close()
        }
    }

    /**
     * Aktualisiert ein bestehendes ToDo in der Datenbank.
     *
     * @param todo Das aktualisierte `ToDo`-Objekt.
     * @return `true`, wenn das Update erfolgreich war, andernfalls `false`.
     */
    fun updateToDo(todo: ToDo): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            val values = ContentValues().apply {
                put("name", todo.name)
                put("priority", todo.priority)
                put("enddate", todo.enddate)
                put("description", todo.description)
                put("state", if (todo.state) 1 else 0) // Update state
            }
            val result = db.update("ToDo", values, "id = ?", arrayOf(todo.id.toString()))
            Log.d("ToDoController", "Updated ToDo: $todo")
            result > 0
        } catch (e: Exception) {
            Log.e("ToDoController", "Error updating ToDo", e)
            false
        } finally {
            db.close()
        }
    }

    /**
     * Löscht ein ToDo aus der Datenbank anhand seiner ID.
     *
     * @param id Die ID des zu löschenden ToDos.
     * @return `true`, wenn das Löschen erfolgreich war, andernfalls `false`.
     */
    fun deleteToDo(id: Int): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            val result = db.delete("ToDo", "id = ?", arrayOf(id.toString()))
            Log.d("ToDoController", "Deleted ToDo with ID: $id")
            result > 0
        } catch (e: Exception) {
            Log.e("ToDoController", "Error deleting ToDo", e)
            false
        } finally {
            db.close()
        }
    }
}
