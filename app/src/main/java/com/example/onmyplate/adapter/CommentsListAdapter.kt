package com.example.onmyplate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.onmyplate.R
import com.example.onmyplate.model.Comment
import com.example.onmyplate.model.FirebaseModel
import com.squareup.picasso.Picasso

class CommentsListAdapter(
    private var comments: MutableList<Comment>?
) : RecyclerView.Adapter<CommentsListAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.userProfileImage)
        var userName: TextView = view.findViewById(R.id.userName)
        val description: TextView = view.findViewById(R.id.commentText)

        fun bind(comment: Comment) {
            FirebaseModel.shared.getUserById(comment.userId) { user ->
                if (user != null) {
                    Picasso.get()
                        .load(user.profilePictureUrl)
                        .into(imageView)
                    userName.text = user.name
                }
            }

            description.text = comment.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_post_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        if (comments?.get(position) != null) {
            FirebaseModel.shared.getUserById(comments!![position].userId) { user ->
                if (user != null) {
                    Picasso.get()
                        .load(user.profilePictureUrl)
                        .into(holder.imageView)
                    holder.userName.text = user.name
                    holder.description.text = comments!![position].description
                }
            }
        }
        holder.bind(comments!![position])
    }

    override fun getItemCount(): Int = comments?.size ?: 0

    fun setComments(comments: MutableList<Comment>) {
        this.comments = comments
    }
}
