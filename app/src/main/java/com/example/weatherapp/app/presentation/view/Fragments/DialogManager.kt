package com.example.weatherapp.app.presentation.view.Fragments

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText

object DialogManager {
    fun LocationSettingsDialog(context: Context, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle("Enable location?")
        dialog.setMessage("Location disabled, do you want enable location?")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
            listener.onClick(null)
            dialog.dismiss()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel"){ _, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }
    fun FindByNameDialog(context: Context, listener: Listener) {
        val builder = AlertDialog.Builder(context)

        val edName = EditText(context)
        builder.setView(edName)

        val dialog = builder.create()
        dialog.setTitle("Enter city name:")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
            listener.onClick(edName.text.toString())
            dialog.dismiss()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel"){ _, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }
    fun SetLanguageDialog(context: Context, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        val languages = arrayOf("en", "ru")

        builder.setTitle("Choose language:")
            .setSingleChoiceItems(languages, -1) { dialog, which ->
                val selectedLanguage = languages[which]
                listener.onClick(selectedLanguage)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()
    }
    interface Listener {
        fun onClick(city_name: String?)
    }
}