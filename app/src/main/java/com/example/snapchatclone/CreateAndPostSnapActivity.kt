package com.example.snapchatclone

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList


class CreateAndPostSnapActivity : AppCompatActivity() {
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPictureFromStorage()
            } else {
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
        }
    }

    lateinit var SnapImage: ImageView
    lateinit var CaptionText: EditText
    lateinit var SendSnapButt:Button
    val imagename=UUID.randomUUID().toString()+".jpg"
    fun getPictureFromStorage() {
        var intent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent,1)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var selectedimage: Uri? = data?.data
        if(requestCode==1 && resultCode== Activity.RESULT_OK && data!=null){
            try {
                var bitmap=MediaStore.Images.Media.getBitmap(this.contentResolver,selectedimage)
                SnapImage.setImageBitmap(bitmap)
                if (bitmap!=null){
                    SendSnapButt.isEnabled=true
                }
            }catch (e:Exception){
                e.printStackTrace()
            }

        }
    }
        fun choosePicture(view: View) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED
            ) {
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            } else {
                getPictureFromStorage()
            }
        }

        fun SendSnap(view: View) {
             var caption: String? = CaptionText.text.toString()
            SendSnapButt.isEnabled=false
            SnapImage.setDrawingCacheEnabled(true)
            SnapImage.buildDrawingCache()
            val bitmap = (SnapImage.getDrawable() as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            val uploadTask: UploadTask = FirebaseStorage.getInstance().reference.child("images").child(imagename).putBytes(data)
            Toast.makeText(this,"Wait image is being processsed!",Toast.LENGTH_LONG).show()
            uploadTask.addOnFailureListener {
                Toast.makeText(this,"Error in uploading",Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener { taskSnapshot ->
                Toast.makeText(this,"Successfully uploaded",Toast.LENGTH_SHORT).show()
                taskSnapshot.storage.downloadUrl.addOnCompleteListener { task ->
                    var fileLink = task.result.toString()
                    Log.i("IMAGEURL", fileLink!!)
                    var intent=Intent(this,SendSnapActivity::class.java)
                    intent.putExtra("Caption",caption)
                    intent.putExtra("ImageName",imagename)
                    intent.putExtra("ImageUrl",fileLink)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent)
                }

            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_create_and_post_snap)
            SnapImage = findViewById(R.id.snapImageView)
            CaptionText = findViewById(R.id.captionText)
            SendSnapButt=findViewById(R.id.sendSnapButton)
            SendSnapButt.isEnabled=false
            var actionBar=supportActionBar
            val colorDrawable = ColorDrawable(Color.parseColor("#3BB9FF"))
            if (actionBar != null) {
                actionBar.setBackgroundDrawable(colorDrawable)
                actionBar.setTitle(Html.fromHtml("<font color='#000000'>Select Image to send </font>"));
            }
        }
    }
