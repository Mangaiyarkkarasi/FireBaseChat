package com.example.myapp.firebasechat

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.sql.Timestamp

@Parcelize
class TextPostData(val textPost:String, val textPostTimestamp: Long, val uid: String, var postLikes:Int, var hasLiked:Boolean, val uKey:String) : Parcelable {
    constructor(): this("",-1,"",0,false,"")
}