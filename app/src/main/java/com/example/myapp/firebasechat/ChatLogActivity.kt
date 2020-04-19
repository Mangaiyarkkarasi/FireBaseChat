package com.example.myapp.firebasechat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG="CHAT"
    }
    var toUser: User?=null
    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

//        val userName= intent.getStringExtra(NewMessageActivity.USERNAME_KEY)
        toUser= intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title=toUser?.userName

        button_chat_send.setOnClickListener {
            Log.d(TAG,"On click")
            performSendMsg()
        }


        recyclerview_chat_msg.adapter=adapter
        //setUpData()
        listenForMessage()
    }

    private fun listenForMessage() {
        val fromId=FirebaseAuth.getInstance().uid
        val toId=toUser?.uid
        val ref= FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMsg= p0.getValue(ChatMessage::class.java)
                Log.d(TAG,chatMsg?.textMsg)
                if(chatMsg!=null){
                    if(chatMsg.fromId==FirebaseAuth.getInstance().uid){
                        adapter.add(ChatFromItem(chatMsg.textMsg,chatMsg.timestamp))
                    }
                    else{
                        adapter.add(ChatToItem(chatMsg.textMsg,chatMsg.timestamp))
                    }
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })
    }

    class ChatMessage(val id:String, val textMsg: String, val fromId:String, val toId:String, val timestamp: Long  ){
        constructor():this("","","","",-1)
    }

    private fun performSendMsg() {
        val textMsg = edittext_chat_msg.text.toString()
        val fromID= FirebaseAuth.getInstance().uid
//        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toID= toUser!!.uid

        Log.d("textmsg",textMsg.isEmpty().toString())
        Log.d("textmss",edittext_chat_msg.text.toString())
        if(fromID==null ) return

//        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromID/$toID").push()

        val toReference=FirebaseDatabase.getInstance().getReference("/user-messages/$toID/$fromID").push()

        val chatMessage = ChatMessage(reference.key!!, textMsg, fromID!!, toID, System.currentTimeMillis())
        reference.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d(TAG,"Saved message: ${reference.key}")
                    //to clear msgg
                    edittext_chat_msg.text.clear()
                    recyclerview_chat_msg.scrollToPosition(adapter.itemCount-1)
                }
        toReference.setValue(chatMessage)

    }

//    fun setUpData(){
//        val adapter = GroupAdapter<ViewHolder>()
//        adapter.add(ChatFromItem("This is froom msg"))
//        adapter.add(ChatToItem("This is tooo msg"))
////        adapter.add(ChatFromItem())
////        adapter.add(ChatToItem())
//
//        recyclerview_chat_msg.adapter=adapter
//
//    }

}


class ChatFromItem(val textFromMsg: String,val timestamp: Long): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_msg.text=textFromMsg
        val date= Date(timestamp)
        val format = SimpleDateFormat("HH:mm")
        viewHolder.itemView.textview_from_time.text=format.format(date)
    }
}


class ChatToItem(val textToMsg: String,val timestampTo: Long): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val date= Date(timestampTo)
        val format = SimpleDateFormat("HH:mm")
        viewHolder.itemView.textview_to_msg.text=textToMsg
        viewHolder.itemView.textview_to_time.text=format.format(date)
    }

}