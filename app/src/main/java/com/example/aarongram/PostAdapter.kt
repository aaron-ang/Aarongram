package com.example.aarongram

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

// TODO Create new ImageAdapter class to bind image only
// TODO Create new item_image xml and set width = height = parent / 3 and inflate it

class PostAdapter(private val context: Context, private val posts: MutableList<Post>) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    fun clear() {
        posts.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvUsername: TextView = itemView.findViewById(R.id.tvUserName)
        private val ivImage: ImageView = itemView.findViewById(R.id.ivImage)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val tvDescUsername: TextView = itemView.findViewById(R.id.tvUserName2)
        private val tvPostTime: TextView = itemView.findViewById(R.id.tvPostTime)

        fun bind(post: Post) {
            tvUsername.text = post.getUser()?.username
            tvDescUsername.text = tvUsername.text.toString()
            tvDescription.text = post.getDescription()
            "${TimeFormatter.getTimeDifference(post.createdAt.toString())} ago".also {
                tvPostTime.text = it
            }
            Glide.with(itemView.context).load(post.getImage()?.url).into(ivImage)
        }
    }
}