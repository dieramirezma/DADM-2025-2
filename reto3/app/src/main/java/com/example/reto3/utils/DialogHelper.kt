package com.example.reto3.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import com.example.reto3.R

/**
 * Utilidad para crear diálogos comunes
 */
object DialogHelper {

    /**
     * Muestra un diálogo simple con mensaje
     */
    fun showMessageDialog(
        context: Context,
        title: String,
        message: String,
        onDismiss: (() -> Unit)? = null
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.dialog_ok) { dialog, _ ->
                dialog.dismiss()
                onDismiss?.invoke()
            }
            .show()
    }

    /**
     * Muestra un diálogo de confirmación
     */
    fun showConfirmDialog(
        context: Context,
        title: String,
        message: String,
        onConfirm: () -> Unit,
        onCancel: (() -> Unit)? = null
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.dialog_yes) { dialog, _ ->
                onConfirm()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.dialog_no) { dialog, _ ->
                onCancel?.invoke()
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Muestra un diálogo para ingresar texto
     */
    fun showInputDialog(
        context: Context,
        title: String,
        message: String,
        hint: String,
        defaultValue: String = "",
        onConfirm: (String) -> Unit,
        onCancel: (() -> Unit)? = null
    ) {
        val input = EditText(context).apply {
            this.hint = hint
            setText(defaultValue)
        }

        val padding = context.resources.getDimensionPixelSize(R.dimen.dialog_padding)
        input.setPadding(padding, padding / 2, padding, padding / 2)

        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setView(input)
            .setCancelable(false)
            .setPositiveButton(R.string.dialog_ok) { dialog, _ ->
                val text = input.text.toString()
                onConfirm(text)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.dialog_cancel) { dialog, _ ->
                onCancel?.invoke()
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Muestra un diálogo de error
     */
    fun showErrorDialog(
        context: Context,
        message: String,
        onDismiss: (() -> Unit)? = null
    ) {
        showMessageDialog(
            context,
            context.getString(R.string.error_unknown),
            message,
            onDismiss
        )
    }

    /**
     * Muestra un diálogo de carga
     */
    fun showLoadingDialog(context: Context, message: String): AlertDialog {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.loading_view, null)

        return AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()
            .also { it.show() }
    }
}
