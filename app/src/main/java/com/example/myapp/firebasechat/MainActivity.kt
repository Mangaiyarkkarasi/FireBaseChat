package com.example.myapp.firebasechat

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_register.setOnClickListener(View.OnClickListener {
            performRegistration()
        })

        textView_login.setOnClickListener {
            val intent= Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performRegistration() {
        val email=editText_email.text.toString()
        val password=editText_password.text.toString()

        Log.d("Email:",email)
        Log.d("password:",password)

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        //firebase authentication to create user with email and password
        Log.d("FAuth.getInstance():",FirebaseAuth.getInstance().toString())

        //FirebaseAuth.getInstance()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener{
                    if(!it.isSuccessful) {
                        //by default it validate email address
                        Log.d("Succesfully not", it.exception?.message)
                    }
                    else{
                        Log.d("Succesfully created", it.result.user.uid)
                        saveUserToFirebase()
                    }
                }

                //can use $ to get the value within double quotes -- koltin feature
                .addOnFailureListener{
                    Log.d("Failure","Failed to create user:${it.message}")
                }
    }

    public fun saveUserToFirebase(){
        Log.d("inside uid","uid")
        val uid=FirebaseAuth.getInstance().uid ?: ""
        Log.d("uid",uid)

        val ref=FirebaseDatabase.getInstance().getReference("/users/$uid")
        Log.d("uid ref",ref.toString())

        val user= User(uid,editText_username.text.toString())

        ref.setValue(user)
                .addOnSuccessListener {
                    Log.d("uid", "Successfully Registered")
                }

                .addOnFailureListener{
                    Log.d("uid", "Failed to add")
                }

        val latestMessagesActivity = Intent(this, LatestMessagesActivity::class.java)
        intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(latestMessagesActivity)
    }
}

@Parcelize
class User(val uid:String, val userName: String):Parcelable{
    constructor(): this("","")
}