package com.example.todoaufgabe9.model

/**
 * Datenklasse, die ein ToDo-Element repräsentiert.
 *
 * Diese Klasse enthält alle notwendigen Attribute, um ein ToDo in der Anwendung zu beschreiben
 * und speichert den Zustand sowie weitere Details eines ToDos.
 *
 * @property id Eindeutige ID des ToDos (wird von der Datenbank automatisch generiert).
 * @property name Der Name oder Titel des ToDos.
 * @property priority Die Prioritätsstufe des ToDos (z. B. 1 = hoch, 2 = mittel, 3 = niedrig).
 * @property enddate Das Enddatum des ToDos im Format 'YYYY-MM-DD HH:MM:SS'.
 * @property description Eine detaillierte Beschreibung des ToDos.
 * @property state Der Status des ToDos: `true` bedeutet erledigt, `false` bedeutet offen.
 */
data class ToDo(
    val id: Int = 0, // Standardwert für die ID, wird von der Datenbank automatisch gesetzt.
    val name: String, // Titel oder Name des ToDos.
    val priority: Int, // Priorität als numerischer Wert.
    val enddate: String, // Enddatum im Format 'YYYY-MM-DD HH:MM:SS'.
    val description: String, // Beschreibungstext für das ToDo.
    val state: Boolean // Status: TRUE = erledigt, FALSE = offen.
)
