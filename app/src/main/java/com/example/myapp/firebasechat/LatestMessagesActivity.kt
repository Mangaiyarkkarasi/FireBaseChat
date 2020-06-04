package com.example.myapp.firebasechat

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_latest_messages.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class LatestMessagesActivity : AppCompatActivity() {

    var fbUser: FirebaseUser? = null
    var storageRef: StorageReference?=null
    var textPostData:MutableList<TextPostData>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)
        storageRef=FirebaseStorage.getInstance().reference

        verifyUserIsLoggedIn()

        getNewFeed()

       rview_post.layoutManager=LinearLayoutManager(this)
        rview_post.adapter=NewFeedAdapter(textPostData, this)

        txt_post_btn.setOnClickListener{
            fbUser = FirebaseAuth.getInstance().getCurrentUser()
            val storageRefPost= FirebaseDatabase.getInstance().getReference("TextPost").push()
            val textPostData=TextPostData(txt_post.text.toString(),System.currentTimeMillis(),FirebaseAuth.getInstance().uid.toString(),0,false,"")
            storageRefPost.setValue(textPostData)
                    .addOnSuccessListener {
                        Log.d("Onsuccess","Posteed Successfully")
                    }
                    .addOnFailureListener{
                        Log.d("OnFailur","Post Fail")
                    }
        }

    }

     private fun getNewFeed() {
        val mDatabase = FirebaseDatabase.getInstance()
        val mDbRef = mDatabase.getReference("TextPost").orderByChild("textPost")

        textPostData= mutableListOf<TextPostData>()
        // fetch reference database

        mDbRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {

                for(d: DataSnapshot in p0.children){
                    Log.d("###",d.key)
                    val h=d.getValue(TextPostData::class.java)
                    val da=TextPostData(h!!.textPost,h.textPostTimestamp,h.uid,h.postLikes,h.hasLiked,d!!.key.toString())
                   textPostData!!.add(da)
                    Log.d("postttt",textPostData.toString())
                    rview_post.adapter!!.notifyDataSetChanged();

                }

            }
        })

    }


    private fun verifyUserIsLoggedIn(){
        val uid=FirebaseAuth.getInstance().uid
        if(uid==null){
            val intent= Intent(this,MainActivity::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menu_new_msg->{
                val intent= Intent(this,NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out->{
                FirebaseAuth.getInstance().signOut()
                val intent= Intent(this,MainActivity::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun setPostLikes(textPostData: TextPostData, uKey: String) {
        Log.d("posttttt",textPostData!!.hasLiked.toString())

        if(!textPostData.hasLiked){
            textPostData.hasLiked=true
            textPostData.postLikes++

        }
        else{
            textPostData.hasLiked=false
            textPostData.postLikes--
        }
        val reff=FirebaseDatabase.getInstance().getReference("TextPost").child(uKey).child("hasLiked").setValue(true)
        Log.d("posttttt ere",reff.toString())

        rview_post.adapter!!.notifyDataSetChanged()

//                reff.addValueEventListener(object : ValueEventListener{
//            override fun onCancelled(p0: DatabaseError) {
//            }
//
//            override fun onDataChange(p0: DataSnapshot) {
//                for(d:DataSnapshot in p0.children){
//                    val key=d.key
//                    val childUpdates = HashMap<String, Any>()
//                    childUpdates.put("/$key/hasLiked", textPostData.hasLiked)
//                    childUpdates.put("/$key/postLikes", textPostData.postLikes)
//                    reff.updateChildren(childUpdates)
//                }
//            }
//
//        })



    }

    fun deletePost(textPostData: TextPostData, uKey: String) {
        FirebaseDatabase.getInstance().getReference("TextPost").child(uKey).removeValue()
        rview_post.adapter!!.notifyDataSetChanged()


//        reff.addValueEventListener(object : ValueEventListener{
//            override fun onCancelled(p0: DatabaseError) {
//            }
//
//            override fun onDataChange(p0: DataSnapshot) {
//                for(d:DataSnapshot in p0.children){
//                    val key=d.key
//                    reff.child("$key").removeValue()
//                }
//            }
//
//        })
    }
}
