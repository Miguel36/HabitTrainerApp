package com.example.habittrainer.db

import android.provider.BaseColumns

const val DATABASE_NAME = "habittrainer.db"
const val DATABASE_VERSION = 10

object HabitEntry : BaseColumns {
    const val TABLE_NAME = "habit"
    const val _ID = "id"
    const val TITLE_COL = "title"
    const val DESCR_COL = "description"
    const val IMAGE_COL = "image"
}