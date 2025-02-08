package com.example.habittrainer.db

import android.content.ContentValues
import android.content.Context
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

        val id = db.insert(HabitEntry.TABLE_NAME, null, values)

        db.close()
        Log.d(TAG, "Stored new habit to the DB $habit")

        return id
    }

    private fun toByteArray(bitmap: Bitmap): ByteArray {
        val stream  = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
    }
}