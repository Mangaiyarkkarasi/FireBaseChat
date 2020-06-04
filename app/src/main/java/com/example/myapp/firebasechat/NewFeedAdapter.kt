package com.example.myapp.firebasechat

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.new_feed_layout.view.*

class NewFeedAdapter(val textPostData: MutableList<TextPostData>?, val latestMessagesActivity: LatestMessagesActivity) : RecyclerView.Adapter<MViewHolder>() {

    override fun onBindViewHolder(mviewHolder: MViewHolder, position: Int) {
        mviewHolder?.txtPostData.text= textPostData!!.get(position).textPost
        //if hasliked true change like icon

        mviewHolder.btnLikes.setOnClickListener({
            latestMessagesActivity.setPostLikes(textPostData.get(position),textPostData.get(position).uKey)
        })
        mviewHolder.imgDeletePost.setOnClickListener({
            latestMessagesActivity.deletePost(textPostData.get(position),textPostData.get(position).uKey)
        })


    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): MViewHolder {
        return MViewHolder(LayoutInflater.from(latestMessagesActivity).inflate(R.layout.new_feed_layout, viewGroup, false))
    }

    override fun getItemCount(): Int {
        return textPostData!!.size
    }


}

class MViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    val txtPostData=itemView.txt_post_text_data
    val btnLikes=itemView.btn_post_like
    val imgDeletePost=itemView.img_delete_post
}
