package com.example.habittrainer.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.util.Log
import com.example.habittrainer.Habit
import java.io.ByteArrayOutputStream

class HabitDbTable(context: Context) {

    private val TAG = HabitDbTable::class.simpleName
    private val dbHelper = HabitTrainerDB(context)

    fun store(habit: Habit): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues()
        values.put(HabitEntry.TITLE_COL, habit.title)
        values.put(HabitEntry.DESCR_COL, habit.description)
        values.put(HabitEntry.IMAGE_COL, toByteArray(habit.image))

        db.transaction {
            db.insert(HabitEntry.TABLE_NAME, null, values)
        }

        Log.d(TAG, "Stored new habit to the DB $habit")
        return id
    }

    private fun toByteArray(bitmap: Bitmap): ByteArray {
        val stream  = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
    }
}

private fun SQLiteDatabase.transaction(function: SQLiteDatabase.() -> Unit) {
    beginTransaction()
    try {
        function()
        setTransactionSuccessful()
    }
    finally {
        endTransaction()
    }
    close()
}