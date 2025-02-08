package com.example.habittrainer

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HabitViewHolder(card: View) : RecyclerView.ViewHolder(card) {

    val txtTitle: TextView = itemView.findViewById(R.id.txt_title)
    val txtDescription: TextView = itemView.findViewById(R.id.txt_description)
    val imgHabit: ImageView = itemView.findViewById(R.id.img_icon)

    fun bind(item: Habit) {
        imgHabit.setImageResource(item.image)
        txtTitle.text = item.title
        txtDescription.text = item.description
    }
}
