package com.example.snapchatclone

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class ViewSnapActivity : AppCompatActivity() {
    lateinit var viewSnapImage:ImageView
    lateinit var CaptionText:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_snap)
        var actionBar=supportActionBar
//        var i=0
//        fun checkI(){
//            if(i==1){
//
//            }
//        }
        val colorDrawable = ColorDrawable(Color.parseColor("#3BB9FF"))
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(colorDrawable)
            actionBar.setTitle(Html.fromHtml("<font color='#000000'>From:"+intent.getStringExtra("From")+"</font>"));
        }
        viewSnapImage=findViewById(R.id.viewSnapImageView)
        CaptionText=findViewById(R.id.viewCaptionText)
        var obj=DownloadFile()
        var img:Bitmap?=null
        try {
            img=obj.execute(intent.getStringExtra("ImageUrl")).get()
            viewSnapImage.setImageBitmap(img)
            CaptionText.setText(intent.getStringExtra("Caption"))
            FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child("snaps").child(intent.getStringExtra("key").toString()).removeValue()
            FirebaseDatabase.getInstance().reference.child("users").addChildEventListener(object:ChildEventListener{
                override fun onCancelled(error: DatabaseError) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if(!snapshot.child("snaps").value.toString().contains(intent.getStringExtra("ImageName").toString())){
                        FirebaseStorage.getInstance().reference.child("images").child(intent.getStringExtra("ImageName").toString()).delete()
                    }
                }
                override fun onChildRemoved(snapshot: DataSnapshot) {}
            })
        }catch (e:Exception){
            e.printStackTrace()
        }
        if(img==null){
            Toast.makeText(this,"Image was unable to download",Toast.LENGTH_SHORT).show()
        }
    }
    class DownloadFile : AsyncTask<String, Void, Bitmap>() {
        override fun doInBackground(vararg urls: String): Bitmap? {
            try {
                var url=URL(urls[0])
                val httpurlconnection : HttpURLConnection= url.openConnection() as HttpURLConnection
                httpurlconnection.connect()
                val inputstream:InputStream=httpurlconnection.inputStream
                val image:Bitmap=BitmapFactory.decodeStream(inputstream)
                return image
            }catch (e:Exception){
                return null
            }
        }

    }
}

