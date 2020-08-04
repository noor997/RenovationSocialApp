package com.noor.newease

import android.app.AlertDialog
import android.content.DialogInterface


abstract class DialogInterface{

    abstract fun onPositiveClicked(dialog: DialogInterface)
    abstract fun onNegativeClicked()
}