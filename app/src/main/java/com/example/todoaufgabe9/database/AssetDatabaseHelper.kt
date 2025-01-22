package com.example.todoaufgabe9.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream

/**
 * Ein Helper zur Verwaltung der SQLite-Datenbank, die aus den Assets geladen wird.
 *
 * Diese Klasse erweitert `SQLiteOpenHelper` und stellt sicher, dass die Datenbank
 * aus dem Assets-Verzeichnis kopiert wird, bevor sie verwendet wird.
 *
 * @param context Der Anwendungskontext.
 */
class AssetDatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    /**
     * Erstellt die Datenbank. Nicht implementiert, da die Datenbank aus den Assets geladen wird.
     *
     * @param db Die SQLite-Datenbank, die erstellt werden soll.
     */
    override fun onCreate(db: SQLiteDatabase?) {
        // Keine Implementierung erforderlich, da die Datenbank aus den Assets geladen wird.
    }

    /**
     * Führt Upgrades für die Datenbank durch, falls die Version geändert wurde.
     * In der aktuellen Implementierung wird keine spezielle Upgrade-Logik benötigt.
     *
     * @param db Die SQLite-Datenbank.
     * @param oldVersion Die alte Version der Datenbank.
     * @param newVersion Die neue Version der Datenbank.
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Logik für zukünftige Datenbank-Updates, falls nötig.
    }

    /**
     * Stellt eine lesbare Datenbank zur Verfügung. Kopiert die Datenbank aus den Assets,
     * falls sie noch nicht existiert.
     *
     * @return Eine lesbare SQLite-Datenbank.
     */
    override fun getReadableDatabase(): SQLiteDatabase {
        copyDatabaseFromAssets()
        return super.getReadableDatabase()
    }

    /**
     * Stellt eine schreibbare Datenbank zur Verfügung. Kopiert die Datenbank aus den Assets,
     * falls sie noch nicht existiert.
     *
     * @return Eine schreibbare SQLite-Datenbank.
     */
    override fun getWritableDatabase(): SQLiteDatabase {
        copyDatabaseFromAssets()
        return super.getWritableDatabase()
    }

    /**
     * Kopiert die Datenbank aus dem Assets-Verzeichnis in das interne Speicherverzeichnis,
     * falls sie dort noch nicht existiert.
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
                android.util.Log.d("AssetDatabaseHelper", "Database copied successfully.")
            } catch (e: Exception) {
                android.util.Log.e("AssetDatabaseHelper", "Error copying database", e)
            }
        }
    }

    companion object {
        /**
         * Der Name der Datenbankdatei. Diese Datei sollte sich im Assets-Verzeichnis befinden.
         */
        const val DATABASE_NAME = "todo_database.db"

        /**
         * Die aktuelle Version der Datenbank.
         */
        const val DATABASE_VERSION = 1
    }
}
