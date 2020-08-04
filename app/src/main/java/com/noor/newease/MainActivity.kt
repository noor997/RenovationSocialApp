package com.noor.newease

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.nfc.Tag

import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
//import com.google.android.gms.ads.AdRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import  kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.tweets_ticket.view.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : BaseActivity() {

    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference
    var DownloadURL: String? = ""
    var postsAdapter: PostsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        iv_attach.setOnClickListener(View.OnClickListener {
            loadImage()

        })

        iv_post.setOnClickListener(View.OnClickListener {
            //upload server
            hideKeyboard()
            showProgress()
            getUserImage(FirebaseAuth.getInstance().currentUser!!.uid,callback = object : DataReturnedInterface{
                override fun onDataReturned(data: String) {
                    myRef.child("posts").push().setValue(
                        PostInfo(
                            FirebaseAuth.getInstance().currentUser!!.uid,
                            etPost.text.toString(), DownloadURL!!,data,System.currentTimeMillis())


                    )
                    etPost.setText("")
                    DownloadURL = ""
                    hideProgress()
                }
            })

        })
        postsAdapter = PostsAdapter(this)
        recycler_posts.adapter = postsAdapter
        loadPost()

    }

    fun loadPost() {
        showProgress()
        myRef.child("posts")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    hideProgress()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    hideProgress()
                    try {
                        val listPosts: ArrayList<Ticket> = ArrayList()
                        var td = dataSnapshot.value as HashMap<*, *>
                        for (key in td.keys) {
                            var post = td[key] as HashMap<*, *>

                            listPosts.add(
                                Ticket(
                                    key as String,
                                    post["text"] as String,
                                    post["postImage"] as String,
                                    post["userUID"] as String,
                                    post["userImage"] as String,
                                    post["postDate"] as Long)
                            )
                        }
                        postsAdapter!!.updateListTickets(listPosts)
                    } catch (ex: Exception) {
                    }
                }
            })

    }

    fun getUserImage(id: String, callback: DataReturnedInterface) {
        myRef.child("Users").child(id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    try {
                        var td = dataSnapshot.value as HashMap<*, *>
                        for (key in td.keys) {
                            if (key.equals("userImage")) {
                                callback.onDataReturned(td["userImage"] as String)
                                break
                            }
                        }

                    } catch (ex: Exception) {
                    }
                }
            })
    }

    //Load image

    val PICK_IMAGE_CODE = 123
    fun loadImage() {

        var intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(intent, PICK_IMAGE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_CODE && data != null && resultCode == RESULT_OK) {

            val selectedImage = data.data
            val filePathColum = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImage!!, filePathColum, null, null, null)
            cursor!!.moveToFirst()
            val coulomIndex = cursor.getColumnIndex(filePathColum[0])
            val picturePath = cursor.getString(coulomIndex)
            cursor.close()
            UploadImage(BitmapFactory.decodeFile(picturePath))
        }

    }


    fun UploadImage(bitmap: Bitmap) {

        val storage = FirebaseStorage.getInstance()
        val storgaRef = storage.getReferenceFromUrl("gs://happymeforeverekrjewkjtwe.appspot.com")
        val df = SimpleDateFormat("ddMMyyHHmmss", Locale("English"))
        val dataobj = Date()
        val imagePath =
            splitString(FirebaseAuth.getInstance().currentUser!!.email!!) + "." + df.format(dataobj) + ".jpg"
        val ImageRef = storgaRef.child("imagePost/" + imagePath)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = ImageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(applicationContext, "fail to upload", Toast.LENGTH_LONG).show()
        }.addOnSuccessListener { taskSnapshot ->
            //fixed
            taskSnapshot.storage.downloadUrl.addOnSuccessListener(OnSuccessListener { tResult ->
                run {
                    DownloadURL = tResult.toString()
                }
            })
        }
    }


    fun splitString(email: String): String {
        val split = email.split("@")
        return split[0]
    }


    fun hideKeyboard(){
        var inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus.windowToken,0)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, SplashScreen::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

