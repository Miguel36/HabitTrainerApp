package com.example.habittrainer

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.habittrainer.databinding.ActivityCreateHabitBinding
import com.example.habittrainer.db.HabitDbTable
import java.io.FileNotFoundException


class CreateHabitActivity : AppCompatActivity() {

    private val TAG = CreateHabitActivity::class.simpleName
    private var selectedImageBitmap: Bitmap? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val selectedImageUri = result.data?.data
            selectedImageUri.let {
                selectedImageBitmap = getBitmapFromUri(it!!)
                binding.previewImage.setImageBitmap(selectedImageBitmap)
            }
        }
    }
    private var _binding: ActivityCreateHabitBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityCreateHabitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chooseImage()
        saveHabit()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun saveHabit() {
        binding.btnSaveHabit.setOnClickListener{
            storeHabit()
        }
    }

    private fun chooseImage() {
        binding.btnChooseImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT

            val chooser = Intent.createChooser(intent, "Choose image for habit")
            getContent.launch(chooser)
            Log.d(TAG, "Intent to choose image sent...")
        }
    }

    private fun storeHabit() {
        if (binding.inputTitleHabit.isBlank() || binding.inputDescHabit.isBlank()) {
            Log.d(TAG, "No habit stored: title or description missing.")
            displayErrorMessage("Your habit needs an engaging title and description.")
            return
        }
        else if (selectedImageBitmap == null) {
            Log.d(TAG, "No habit stored: image missing.")
            displayErrorMessage("Add a motivating picture to your habit.")
            return
        }

        // Store the habit
        val title = binding.inputTitleHabit.text.toString()
        val description = binding.inputDescHabit.text.toString()
        val habit = Habit(title, description, selectedImageBitmap!!)

        val id = HabitDbTable(applicationContext).store(habit)
        if (id == -1L) {
            displayErrorMessage("Habit could not be stored...let's not make this a habit")
        }
        else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun displayErrorMessage(message: String) {
        binding.txtInputError.text = message
        binding.txtInputError.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // Función para convertir Uri a Bitmap
    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }
}

// We create the extension function for simplify and make more readable our code,
// In this case, the inputs
private fun EditText.isBlank() = this.text.toString().isBlank()
