package com.example.snapchatclone

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class SnapActivity : AppCompatActivity() {

    lateinit var  mySnapFeed:ListView;
     var maplist=ArrayList<DataSnapshot>()
    var mySnapList=ArrayList<String>()
    lateinit var arrayAdapter:ArrayAdapter<String>;
    val mAuth=FirebaseAuth.getInstance()
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater:MenuInflater=menuInflater
        inflater.inflate(R.menu.snap_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.newSnap)
        {
            val intent:Intent=Intent(this,CreateAndPostSnapActivity::class.java)
            startActivity(intent)
        }else if(item.itemId==R.id.logOut){
            mAuth.signOut()
            val intent:Intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snap)
        var actionBar=supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#3BB9FF"))
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(colorDrawable)
            actionBar.setTitle(Html.fromHtml("<font color='#000000'>"+FirebaseAuth.getInstance().currentUser?.email.toString()+"'s Home"+"</font>"));
        }
        mySnapFeed=findViewById<ListView>(R.id.mySnapFeed);
        arrayAdapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,mySnapList)
        mySnapFeed.adapter=arrayAdapter
        mySnapFeed.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                var intent=Intent(this,ViewSnapActivity::class.java)
                intent.putExtra("From",mySnapList.get(position))
            intent.putExtra("key",maplist.get(position).key.toString())
                intent.putExtra("ImageUrl",(maplist.get(position).value as Map<String,String>)["ImageUrl"].toString())
                intent.putExtra("ImageName",(maplist.get(position).value as Map<String,String>)["ImageName"].toString())
                intent.putExtra("Caption",(maplist.get(position).value as Map<String,String>)["Caption"].toString())
                Toast.makeText(this,"Loading Snap...",Toast.LENGTH_LONG).show()
                mySnapList.removeAt(position)
                maplist.removeAt(position)
                arrayAdapter.notifyDataSetChanged()
                startActivity(intent)
            }

            FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child("snaps").addChildEventListener(object : ChildEventListener {

                override fun onCancelled(error: DatabaseError) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                maplist.add(snapshot)
                mySnapList.add((snapshot.value as Map<String, String>)["from"].toString())
                arrayAdapter.notifyDataSetChanged()
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {}

        })

    }
}