package com.example.habittrainer

import android.content.Intent
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


class CreateHabitActivity : AppCompatActivity() {

    private val TAG = CreateHabitActivity::class.simpleName
    private var selectedImageUri: Uri? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            selectedImageUri = result.data?.data
            selectedImageUri.let {
                binding.previewImage.setImageURI(it)
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
        else if (selectedImageUri == null) {
            Log.d(TAG, "No habit stored: image missing.")
            displayErrorMessage("Add a motivating picture to your habit.")
            return
        }

        // Store the habit
        binding.txtInputError.visibility = View.GONE
    }

    private fun displayErrorMessage(message: String) {
        binding.txtInputError.text = message
        binding.txtInputError.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}

// We create the extension function for simplify and make more readable our code,
// In this case, the inputs
private fun EditText.isBlank() = this.text.toString().isBlank()
