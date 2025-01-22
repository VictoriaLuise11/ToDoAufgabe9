package com.example.todoaufgabe9

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.todoaufgabe9.ui.screen.ToDoScreen

/**
 * Die MainActivity ist der Einstiegspunkt der ToDo-App.
 * Sie initialisiert die App und setzt die Benutzeroberfläche auf den Hauptbildschirm.
 */
class MainActivity : ComponentActivity() {

    /**
     * Wird aufgerufen, wenn die Aktivität erstellt wird.
     * Hier wird die Edge-to-Edge-Darstellung aktiviert und der Hauptinhalt der App gesetzt.
     *
     * @param savedInstanceState Falls vorhanden, enthält dies den zuvor gespeicherten Zustand der Aktivität.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Aktiviert eine durchgehende Darstellung für modernes Design.

        setContent {
            // Ruft den Hauptbildschirm der App auf, der die ToDo-Liste anzeigt.
            ToDoScreen(context = this)
        }
    }
}
