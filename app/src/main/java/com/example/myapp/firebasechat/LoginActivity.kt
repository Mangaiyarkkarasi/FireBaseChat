package com.example.myapp.firebasechat

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        button_login.setOnClickListener{
            val email=editText_login_email.text.toString()
            val password=editText_login_password.text.toString()

            val ref=FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
           // Log.d("ref*****",ref.result.toString())
            ref.addOnCompleteListener{
                if(!it.isSuccessful)
                    return@addOnCompleteListener
                val latestMessagesActivity = Intent(this, LatestMessagesActivity::class.java)
                intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(latestMessagesActivity)
            }
                    .addOnFailureListener{
                        Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
        }

        textView_back.setOnClickListener {
            finish()
        }
    }
}
