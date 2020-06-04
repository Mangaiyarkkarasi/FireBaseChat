package com.example.myapp.firebasechat

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
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
import kotlinx.android.synthetic.main.chat_date_item.view.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.database.ValueEventListener


class ChatLogActivity : AppCompatActivity() {

    private val sharedPrefFile = "appDataSharedPreference"
    private var chatCountToStore=0;
    companion object {
        var TAG="CHAT"
    }

    var toUser: User?=null
    val adapter = GroupAdapter<ViewHolder>()
    var prevDate:String="PREVDATE"
    var sharedPreferences: SharedPreferences?=null
    var editor:SharedPreferences.Editor?=null

    private var chatCount: Int=0

    private var price: Double=0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        sharedPreferences= this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
//        val userName= intent.getStringExtra(NewMessageActivity.USERNAME_KEY)
        toUser= intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title=toUser?.userName

        chatCountToStore= sharedPreferences?.getInt("chat_count",0)!!
        Log.d("$$$$$$$$",chatCountToStore.toString())

        // fetch reference database
        val mDatabase = FirebaseDatabase.getInstance()
        val mDbRef = mDatabase.getReference("app-data")

        mDbRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val h=p0.getValue(AppData::class.java)
                    chatCount=h!!.chatCount
                    price=h.price
                    Log.d("#####",h.chatCount.toString())
                }
            }

        })



        button_chat_send.setOnClickListener {
            Log.d(TAG,"On click")

            if (chatCountToStore != null) {
                if(chatCountToStore<chatCount){
                    performSendMsg()
                }
                else{
                    showDialog()
                }
            }

        }


        recyclerview_chat_msg.adapter=adapter
        //setUpData()
        listenForMessage()
    }

    private fun showDialog() {
        // Late initialize an alert dialog object
        lateinit var dialog: AlertDialog

        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(this)

        // Set a title for alert dialog
        //builder.setTitle("Title of AlertDialog.")

        // Set a message for alert dialog
        builder.setMessage("Purchase a plan for ${price}$ to proceed further.")

        // On click listener for dialog buttons
        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE -> {
                    Log.d("clicked","pos")
                    val paymentFragment=PaymentFragment()
                    val transaction = fragmentManager.beginTransaction()
                    transaction.replace(R.id.chat_main_layout, paymentFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    Log.d("clicked","nrg")

                }
            }
        }

        // Set the alert dialog positive/yes button
        builder.setPositiveButton("Buy Now",dialogClickListener)

        // Set the alert dialog negative/no button
        builder.setNegativeButton("Not now, later",dialogClickListener)

        // Initialize the AlertDialog using builder object
        dialog = builder.create()

        // Finally, display the alert dialog
        dialog.show()

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
                    val cDate=getFormattedDate(chatMsg.timestamp)
                    if(cDate!=null && prevDate!=cDate){
                        adapter.add(ChatDateItem(cDate))
                        prevDate = cDate
                    }
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
        TAG="daata"
        val textMsg = edittext_chat_msg.text.toString()
        val fromID= FirebaseAuth.getInstance().uid
        val toID= toUser!!.uid

        val appDataReference= FirebaseDatabase.getInstance().getReference("app-data/chatCount")
        Log.d("%%%",appDataReference.toString())


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
                    Log.d("chatcountfromshate",(chatCountToStore+1).toString())
                    editor = sharedPreferences?.edit()

                    editor?.putInt("chat_count",chatCountToStore++)
                    editor?.apply()
                    editor?.commit()
                    //to clear msgg
                    edittext_chat_msg.text.clear()
                    recyclerview_chat_msg.scrollToPosition(adapter.itemCount-1)
                }


        toReference.setValue(chatMessage)

    }


    fun getFormattedDate(timeStampInMills:Long ):String{
        val currentDate=Calendar.getInstance()
        currentDate.timeInMillis=timeStampInMills
        val now=Calendar.getInstance()
        val dateFormatInString="EEEE, MMMM d"
        if(now.get(Calendar.DATE)==currentDate.get(Calendar.DATE)){
            return "Today"
        }
        else if((now.get(Calendar.DATE)-currentDate.get(Calendar.DATE))==1){
            return "Yesterday"
        }
        else{
            val dateFormat=SimpleDateFormat(dateFormatInString,Locale.US)
            return dateFormat.format(timeStampInMills)
        }
    }
}


class ChatFromItem(val textFromMsg: String,val timestamp: Long): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_msg.text=textFromMsg
        val date= Date(timestamp)
        val format = SimpleDateFormat("HH:mm",Locale.US)
        viewHolder.itemView.textview_from_time.text=format.format(date)
    }
}


class ChatToItem(val textToMsg: String,val timestampTo: Long): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val date= Date(timestampTo)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.US)
        viewHolder.itemView.textview_to_msg.text=textToMsg
        viewHolder.itemView.textview_to_time.text=format.format(date)
    }
}


class ChatDateItem(val dateToShow:String):Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_date_item
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_chat_date.text=dateToShow

    }
}