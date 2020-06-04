package com.example.myapp.firebasechat

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add_post.*

class AddPostActivity : AppCompatActivity() {

    private var mUrl=""
    private var imageUrl=""
    private var storagePostPicRef: StorageReference?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        storagePostPicRef=FirebaseStorage.getInstance().reference.child("Post pictures")
        //imgview_addpost_save.setOnClickListener(uploadImage())


    }

   private fun uploadImage(){

   }
}
