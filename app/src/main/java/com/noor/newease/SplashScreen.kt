package com.noor.newease
import android.content.Intent

import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val isUserLogged = checkUser()

        //4second splash time
        Handler().postDelayed({
            var intent : Intent?=null
            if (isUserLogged){
                 intent = Intent(this, MainActivity::class.java)
            }else{
                intent = Intent(this, Login::class.java)
            }
            //start main activity
            startActivity(intent)
            //finish this activity
            finish()
        },4000)

    }

    private fun checkUser() : Boolean{
        return  FirebaseAuth.getInstance().currentUser !=null &&
                !TextUtils.isEmpty(FirebaseAuth.getInstance().currentUser!!.email)
                && !TextUtils.isEmpty(FirebaseAuth.getInstance().currentUser!!.uid)
    }

}