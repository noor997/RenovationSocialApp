 package com.noor.newease

 import android.app.AlertDialog
 import android.content.Intent
 import android.content.pm.PackageManager
 import android.graphics.Bitmap
 import android.graphics.BitmapFactory
 import android.os.Build
 import android.os.Bundle
 import android.provider.MediaStore
 import android.text.TextUtils
 import android.view.View
 import android.view.WindowManager
 import android.widget.Toast
 import androidx.core.app.ActivityCompat
 import com.google.android.gms.tasks.OnFailureListener
 import com.google.android.gms.tasks.OnSuccessListener
 import com.google.firebase.auth.FirebaseAuth
 import com.google.firebase.database.FirebaseDatabase
 import com.google.firebase.storage.FirebaseStorage
 import kotlinx.android.synthetic.main.activity_login.*
 import java.io.ByteArrayOutputStream


 class Login : BaseActivity() {


     private var mAuth:FirebaseAuth?=null
     val readImage: Int = 253
     private var database=FirebaseDatabase.getInstance()
     private var myRef=database.reference
     private var userImage : Bitmap ?=null

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
             WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_login)
        mAuth=FirebaseAuth.getInstance()

         ivImagePerson.setOnClickListener( View.OnClickListener {
             checkPermission()
         })

     }
     fun btnLogin(view:View){
         val email = etEmail.text.toString()
         val password  = etPassword.text.toString()
         if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || userImage == null){

             showDialog("error","Please enter you email, password and image","Ok",
                 "cancel",listener = object :DialogInterface(){
                     override fun onPositiveClicked(dialog: android.content.DialogInterface) {
                         dialog.dismiss()
                     }

                     override fun onNegativeClicked() {

                     }
                 })
             return
         }

         saveImageInFirebase(email,password,userImage!!)
     }


     private fun loginToFireBase(email: String, password: String,image: String) {
         showProgress()
         mAuth!!.signInWithEmailAndPassword(email,password)
             .addOnFailureListener(OnFailureListener {
                 createUser(email,password,image)
             })
             .addOnSuccessListener(OnSuccessListener {
             if (it.user == null){
                 createUser(email,password,image)
             }else{
                 hideProgress()
                 starMainActivity()
             }
         })

     }

     fun createUser(email: String, password: String,image: String){
         mAuth!!.createUserWithEmailAndPassword(email, password)
             .addOnCompleteListener(this) { task ->
                 hideProgress()
                 if (task.isSuccessful) {
                     var currentUser = mAuth!!.currentUser
                     if (currentUser != null) {
                         myRef.child("Users").child(currentUser.uid).setValue(UserInfo(currentUser.uid,image))
                         Toast.makeText(applicationContext, "Successful login", Toast.LENGTH_LONG)
                             .show()

                     }
                     starMainActivity()
                 } else {
                     Toast.makeText(applicationContext, "fail login", Toast.LENGTH_LONG).show()
                 }
             }
     }


     fun saveImageInFirebase(email:String ,password:String , image:Bitmap){
         showProgress("Uploading image, please wait")
         val storage= FirebaseStorage.getInstance()
         val storgaRef=storage.getReferenceFromUrl("gs://happymeforeverekrjewkjtwe.appspot.com")
         val imagePath = System.currentTimeMillis().toString() + ".jpg"
         val ImageRef=storgaRef.child("images/"+imagePath )
         ivImagePerson.isDrawingCacheEnabled=true
         ivImagePerson.buildDrawingCache()

         val baos= ByteArrayOutputStream()
         image.compress(Bitmap.CompressFormat.JPEG,100,baos)
         val data= baos.toByteArray()
         val uploadTask=ImageRef.putBytes(data)
         uploadTask.addOnFailureListener{
             hideProgress()
             Toast.makeText(applicationContext,"fail to upload",Toast.LENGTH_LONG).show()
         }.addOnSuccessListener { taskSnapshot ->
             taskSnapshot.storage.downloadUrl.addOnSuccessListener(OnSuccessListener {
                 tResult ->
                 run {
                     loginToFireBase(email,password, tResult.toString())
                 }
             })
             hideProgress()
         }

     }

     private fun starMainActivity() {
         startActivity(Intent(this, MainActivity::class.java))
         finish()
     }


     fun checkPermission() {

         if (Build.VERSION.SDK_INT >= 23) {
             if (ActivityCompat.checkSelfPermission(
                     this,
                     android.Manifest.permission.READ_EXTERNAL_STORAGE
                 ) !=
                 PackageManager.PERMISSION_GRANTED
             ) {

                 requestPermissions(
                     arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                     readImage
                 )
                 return
             }
         }

         loadImage()
     }

     //  for image access
     override fun onRequestPermissionsResult(
         requestCode: Int,
         permissions: Array<out String>,
         grantResults: IntArray
     ) {

         when (requestCode) {
             readImage -> {
                 if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                     loadImage()
                 } else {
                     Toast.makeText(
                         applicationContext,
                         "Cannot access your images",
                         Toast.LENGTH_LONG
                     ).show()
                 }
             }
             else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
         }


     }


     val pickimagecode = 123
     fun loadImage() {

         //android provider before media
         var intent = Intent(
             Intent.ACTION_PICK,
             MediaStore.Images.Media.EXTERNAL_CONTENT_URI
         )
         startActivityForResult(intent, pickimagecode)
     }


     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)

         if (requestCode == pickimagecode && data != null && resultCode == RESULT_OK) {

             val selectedImage = data.data
             val filePathColum = arrayOf(MediaStore.Images.Media.DATA)
             val cursor = contentResolver.query(selectedImage!!, filePathColum, null, null, null)
             cursor!!.moveToFirst()
             val coulomIndex = cursor.getColumnIndex(filePathColum[0])
             val picturePath = cursor.getString(coulomIndex)
             cursor.close()
             userImage = BitmapFactory.decodeFile(picturePath)
             ivImagePerson.setImageBitmap(userImage)
         }

     }
 }