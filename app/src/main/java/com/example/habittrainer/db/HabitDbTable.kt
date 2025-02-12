package com.example.habittrainer.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import com.example.habittrainer.Habit
import com.example.habittrainer.db.HabitEntry.DESCR_COL
import com.example.habittrainer.db.HabitEntry.IMAGE_COL
import com.example.habittrainer.db.HabitEntry.TABLE_NAME
import com.example.habittrainer.db.HabitEntry.TITLE_COL
import com.example.habittrainer.db.HabitEntry._ID
import java.io.ByteArrayOutputStream

class HabitDbTable(context: Context) {

    private val TAG = HabitDbTable::class.simpleName
    private val dbHelper = HabitTrainerDB(context)

    fun store(habit: Habit): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues()
        with(values) {
            put(TITLE_COL, habit.title)
            put(DESCR_COL, habit.description)
            put(IMAGE_COL, toByteArray(habit.image))
        }

        // The type is Long because insert operation is a function that return long
        val id: Long = db.transaction {
            db.insert(TABLE_NAME, null, values)
        }

        Log.d(TAG, "Stored new habit to the DB $habit")
        return id
    }

    fun readAllHabits(): List<Habit> {
        val columns = arrayOf(_ID, TITLE_COL, DESCR_COL, IMAGE_COL)
        val order = "${_ID} ASC"
        val db = dbHelper.readableDatabase

        val cursor = db.setQuery(colums = columns, order = order)
        val habits = getHabitsFrom(cursor)

        return habits
    }

    private fun getHabitsFrom(cursor: Cursor): MutableList<Habit> {
        val habits = mutableListOf<Habit>()
        while (cursor.moveToNext()) {
            val title = cursor.getColumValue(TITLE_COL)
            val desc = cursor.getColumValue(DESCR_COL)
            val bitmap = cursor.getBitmap(IMAGE_COL)

            habits.add(Habit(title, desc, bitmap))
        }
        cursor.close()
        return habits
    }

    private fun toByteArray(bitmap: Bitmap): ByteArray {
        val stream  = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
    }
}

private inline fun <T> SQLiteDatabase.transaction(function: SQLiteDatabase.() -> T): T {
    beginTransaction()
    val result = try {
        val functionResult = function()
        setTransactionSuccessful()

        functionResult
    }
    finally {
        endTransaction()
    }
    close()

    return result
}

private fun SQLiteDatabase.setQuery(tableName: String = HabitEntry.TABLE_NAME, colums: Array<String>, order: String = "${HabitEntry._ID} ASC"): Cursor {
    return query(tableName, colums, null, null, null, null, order)
}

private fun Cursor.getColumValue(columName: String): String {
    val columIndex = getColumnIndex(columName)
    if (columIndex >= 0) {
        return getString(columIndex)
    }
    return "0"
}

private fun Cursor.getBitmap(columName: String) : Bitmap {
    val columIndex = getColumnIndex(columName)
    if (columIndex >= 0) {
        val bytes = getBlob(columIndex)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
    return getDefaultBitmap()
}

// Función para devolver un Bitmap predeterminado en caso de que la imagen no esté disponible
private fun getDefaultBitmap(): Bitmap {
    // Crear un Bitmap transparente de 1x1 píxel
    return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888).apply {
        eraseColor(Color.TRANSPARENT)
    }
}