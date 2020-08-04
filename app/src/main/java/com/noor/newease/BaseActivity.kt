package com.noor.newease

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

open class BaseActivity : AppCompatActivity() {

    var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Please wait...")
        progressDialog!!.setCancelable(false)

    }

    protected fun showProgress(msg: String) {
        progressDialog!!.setMessage(msg)
        progressDialog!!.show()
    }

    protected fun showProgress() {
        progressDialog!!.show()
    }

    fun hideProgress() {
        progressDialog!!.hide()
    }

    fun showDialog(
        title:String, msg:String,
        postiveBtn:String,
        negativeBtn:String,
        listener: com.noor.newease.DialogInterface
    ){
        var builder : AlertDialog.Builder  = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setPositiveButton(postiveBtn, DialogInterface.OnClickListener { dialog, which ->
            listener.onPositiveClicked(dialog)
        })
        builder.setNegativeButton(negativeBtn, DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
            listener.onNegativeClicked()
        })
        builder.show()

    }
}
