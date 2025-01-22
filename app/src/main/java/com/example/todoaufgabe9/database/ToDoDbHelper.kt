package com.example.todoaufgabe9.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream

/**
 * SQLiteOpenHelper-Implementierung zur Verwaltung einer SQLite-Datenbank,
 * die aus den App-Assets geladen wird.
 *
 * Diese Klasse stellt Methoden bereit, um sicherzustellen, dass die Datenbank
 * korrekt aus den Assets kopiert und genutzt wird.
 *
 * @param context Der Anwendungskontext.
 */
class ToDoDbHelper(val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    /**
     * Wird beim erstmaligen Erstellen der Datenbank aufgerufen.
     * Diese Methode bleibt leer, da die Datenbank aus den Assets geladen wird.
     *
     * @param db Die SQLite-Datenbankinstanz.
     */
    override fun onCreate(db: SQLiteDatabase?) {
        // Leere Methode, da die Datenbank aus den Assets geladen wird.
    }

    /**
     * Wird aufgerufen, wenn die Datenbankversion aktualisiert wird.
     * Die alte Datenbank wird gelöscht und die neue Version aus den Assets kopiert.
     *
     * @param db Die SQLite-Datenbankinstanz.
     * @param oldVersion Die aktuelle Version der Datenbank.
     * @param newVersion Die neue Version der Datenbank.
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Alte Datenbank löschen und neue aus Assets kopieren
        context.deleteDatabase(DATABASE_NAME)
        copyDatabaseFromAssets()
    }

    /**
     * Überschreibt `getReadableDatabase`, um sicherzustellen,
     * dass die Datenbank aus den Assets kopiert wurde, bevor sie geöffnet wird.
     *
     * @return Die lesbare Datenbankinstanz.
     */
    override fun getReadableDatabase(): SQLiteDatabase {
        copyDatabaseFromAssets()
        return super.getReadableDatabase()
    }

    /**
     * Überschreibt `getWritableDatabase`, um sicherzustellen,
     * dass die Datenbank aus den Assets kopiert wurde, bevor sie geöffnet wird.
     *
     * @return Die beschreibbare Datenbankinstanz.
     */
    override fun getWritableDatabase(): SQLiteDatabase {
        copyDatabaseFromAssets()
        return super.getWritableDatabase()
    }

    /**
     * Kopiert die Datenbank aus den Assets, falls sie nicht bereits existiert.
     * Die Datenbank wird in den Standardpfad der App-Datenbank kopiert.
     */
    private fun copyDatabaseFromAssets() {
        val dbPath = context.getDatabasePath(DATABASE_NAME)
        if (!dbPath.exists()) {
            try {
                context.assets.open(DATABASE_NAME).use { inputStream ->
                    FileOutputStream(dbPath).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                android.util.Log.d("ToDoDbHelper", "Database copied successfully to: ${dbPath.absolutePath}")
            } catch (e: Exception) {
                android.util.Log.e("ToDoDbHelper", "Error copying database", e)
            }
        } else {
            android.util.Log.d("ToDoDbHelper", "Database already exists at: ${dbPath.absolutePath}")
        }
    }

    companion object {
        // Name der Datenbankdatei, die aus den Assets kopiert wird.
        const val DATABASE_NAME = "ToDo.db"

        // Version der Datenbank. Erhöhen Sie diesen Wert, wenn Änderungen an der Struktur vorgenommen werden.
        const val DATABASE_VERSION = 1
    }
}
