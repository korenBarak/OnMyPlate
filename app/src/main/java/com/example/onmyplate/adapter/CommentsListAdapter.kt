package com.example.onmyplate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.onmyplate.R
import com.example.onmyplate.model.Comment
import com.example.onmyplate.model.FirebaseModel
import com.example.onmyplate.model.Post
import com.example.onmyplate.model.User
import com.squareup.picasso.Picasso

class CommentsListAdapter(
    private var comments: List<Comment>?,
    private val onRowClicked: (Comment?) -> Unit
) : RecyclerView.Adapter<CommentsListAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.userProfileImage)
        var userName: TextView = view.findViewById(R.id.userName)
        val description: TextView = view.findViewById(R.id.commentText)
        val ratingBar: RatingBar = view.findViewById(R.id.commentRatingBar)

        fun bind(comment: Comment) {
            FirebaseModel().getUserById(comment.userId) { user ->
                if (user != null) {
                    Picasso.get()
                        .load(user.profilePictureUrl)
                        .into(imageView)
                    userName.text = user.name
                }
            }
            description.text = comment.description
            ratingBar.rating = (comment.rating ?: 1) as Float
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_post_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        if (comments?.get(position) != null) {
            FirebaseModel().getUserById(comments!![position].userId) { user ->
                if (user != null) {
                    Picasso.get()
                        .load(user.profilePictureUrl)
                        .into(holder.imageView)
                    holder.userName.text = user.name
                    holder.description.text = comments!![position].description
                    holder.ratingBar.rating = comments!![position].rating
                }
            }
        }
        holder.bind(comments!![position])
    }

    override fun getItemCount(): Int = comments?.size ?: 0

    fun setComments(comments: List<Comment>) {
        this.comments = comments
    }
}
