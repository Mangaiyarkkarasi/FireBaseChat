package com.example.myapp.firebasechat

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title="Select User"

//        val adapter=GroupAdapter<ViewHolder>()
//
//        adapter.add(UserItem(user))
//        adapter.add(UserItem())
//        adapter.add(UserItem())
//
//        recycler_newmessage.adapter= adapter

        fetchUsers()
    }

    companion object {
       // val USERNAME_KEY="USERNAME_KEY"
        val USER_KEY="USER_KEY"

    }

    private fun fetchUsers(){
        val ref= FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val adapter=GroupAdapter<ViewHolder>()
                p0.children.forEach{
                    Log.d("New Message",it.toString())
                    val user=it.getValue(User::class.java)
                    if(user!=null && user.uid!=FirebaseAuth.getInstance().uid){
                        adapter.add(UserItem(user))
                    }
                }
                adapter.setOnItemClickListener{item,view->
                    val userItem = item as UserItem
                    val intent= Intent(view.context,ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY,userItem.user)
                    startActivity(intent)
                    finish()
                }
                recycler_newmessage.adapter=adapter
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
}

class UserItem(val user: User): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_newmsg_userrname.text=user.userName
        //will be called in list for each item

    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}
