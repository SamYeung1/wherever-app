package com.samyeung.wherever.util.helper

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog

object AlertUtils {
    fun create(context: Context, title: String, message: String, onCancel: (DialogInterface) -> Unit, onOk: (DialogInterface) -> Unit): AlertDialog {
        return AlertDialog.Builder(context).setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,
                        { dialog, whichButton ->
                            onOk(dialog)

                        }
                )
                .setNegativeButton(android.R.string.cancel,
                        { dialog, whichButton ->
                            onCancel(dialog)
                        }
                )
                .create()
    }
    fun createOnlyOK(context: Context, title: String, message: String, onOk: (DialogInterface) -> Unit,closable:Boolean = false): AlertDialog {
        return AlertDialog.Builder(context).setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,
                        { dialog, whichButton ->
                            onOk(dialog)

                        }
                )
                .setCancelable(closable)
                .create()
    }
}