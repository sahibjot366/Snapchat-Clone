package com.example.snapchatclone


import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {

     lateinit var usernameText:AutoCompleteTextView
     lateinit var passwordText:EditText
    lateinit var snapchatimg:ImageView
    lateinit var layout:ConstraintLayout
    val mauth=FirebaseAuth.getInstance()
    fun logIn(){
        val intent:Intent=Intent(this,SnapActivity::class.java)
        startActivity(intent)
        finish()
    }
    fun logInSignUp(view:View){
        var User:String=usernameText.text.toString()
        var Password:String=passwordText.text.toString()
        var numberOfUsers:Int
        mauth.signInWithEmailAndPassword(User, Password)
                .addOnCompleteListener(this, OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        logIn();
                    } else {
                        mauth.createUserWithEmailAndPassword(User, Password)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        FirebaseDatabase.getInstance().getReference().child("users").child(
                                            task.result?.user?.uid.toString()
                                        ).child("email").setValue(User)
//                                        FirebaseDatabase.getInstance().reference.child("numberOfUsers").addListenerForSingleValueEvent(object : ValueEventListener {
//                                            override fun onCancelled(error: DatabaseError) {}
//                                            override fun onDataChange(snapshot: DataSnapshot) {
//                                                numberOfUsers=snapshot.child("number").value as Int
//                                                numberOfUsers=numberOfUsers+1
//                                                FirebaseDatabase.getInstance().reference.child("numberOfUsers").remove()
//                                                FirebaseDatabase.getInstance().reference.child("numberOfUsers").child("number").setValue((numberOfUsers).toString())
//                                            }
//                                        })
                                        FirebaseDatabase.getInstance().reference.child("numberOfUsers").addChildEventListener(object : ChildEventListener {

                                            override fun onCancelled(error: DatabaseError) {}
                                            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                                            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                                            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                                                numberOfUsers=snapshot.value as Int
                                                numberOfUsers=numberOfUsers+1
                                                FirebaseDatabase.getInstance().reference.child("numberOfUsers").child(snapshot.key.toString()).setValue(numberOfUsers)
                                            }
                                            override fun onChildRemoved(snapshot: DataSnapshot) {}

                                        })
                                        logIn()
                                    } else {
                                        Toast.makeText(this,"Error Occured!!",Toast.LENGTH_SHORT).show()
                                    }
                                }
                    }
                })

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var actionBar=supportActionBar
        FirebaseDatabase.getInstance().reference.child("numberOfUsers").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i("no.ofusers",snapshot.child("number").value.toString())
//                                                FirebaseDatabase.getInstance().reference.child("numberOfUsers").child("number").removeValue()
//                                                FirebaseDatabase.getInstance().reference.child("numberOfUsers").child("number").setValue(numberOfUsers+1)
            }
        })
        val colorDrawable = ColorDrawable(Color.parseColor("#3BB9FF"))
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(colorDrawable)
            actionBar.setTitle(Html.fromHtml("<font color='#000000'>Snapchat Login </font>"));
        }
        usernameText=findViewById<AutoCompleteTextView>(R.id.usernameText)
        passwordText=findViewById<EditText>(R.id.passwordText)
        snapchatimg=findViewById<ImageView>(R.id.snapchatimageview);
        layout=findViewById<ConstraintLayout>(R.id.mainLayout);
        supportActionBar?.hide()
    if(mauth.currentUser!=null){
        logIn();
    }
    }
}