package com.example.snapchatclone

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase


class SendSnapActivity : AppCompatActivity() {
    lateinit var sendtolist:ListView
    var usersList=ArrayList<String>()
    var snapshotList=ArrayList<String>()
    lateinit var arrayadapter:ArrayAdapter<String>
    override fun onBackPressed() {
        super.onBackPressed()
        var intent:Intent=Intent(this,SnapActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_snap)
        var actionBar=supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#3BB9FF"))
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(colorDrawable)
            actionBar.setTitle(Html.fromHtml("<font color='#000000'>Send Image to friends </font>"));
        }
        sendtolist = findViewById(R.id.sendToList)
        sendtolist.choiceMode = AbsListView.CHOICE_MODE_MULTIPLE
        arrayadapter = ArrayAdapter(this, android.R.layout.simple_list_item_checked, usersList)
        sendtolist.adapter = arrayadapter
        sendtolist.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
//            Toast.makeText(this,"position selected:"+position,Toast.LENGTH_LONG).show()
            val check = view as CheckedTextView
            if (check.isEnabled == true) {
                if (check.isChecked == true) {
                    val map0: Map<String, String> = mapOf("from" to FirebaseAuth.getInstance().currentUser?.email.toString(), "ImageUrl" to intent.getStringExtra("ImageUrl").toString(), "ImageName" to intent.getStringExtra("ImageName").toString(), "Caption" to intent.getStringExtra("Caption").toString())
                    FirebaseDatabase.getInstance().reference.child("users").child(snapshotList.get(position)).child("snaps").push().setValue(map0)
                    Toast.makeText(this@SendSnapActivity, "Snap sent to " + usersList.get(position), Toast.LENGTH_SHORT).show()
                    check.isEnabled = false
                }
            }

        }
        FirebaseDatabase.getInstance().reference.child("users").addChildEventListener(object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(!FirebaseAuth.getInstance().currentUser?.email.toString().equals(snapshot.child("email").value as String))
                {usersList.add(snapshot.child("email").value as String)
                snapshotList.add(snapshot.key.toString())
                arrayadapter.notifyDataSetChanged()}
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}

        })

    }


}