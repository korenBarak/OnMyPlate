package com.example.onmyplate.adapter;

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.onmyplate.R
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso

interface onDeleteButtonClickListener {
    fun onItemClick(photo: ImageData?)
}

sealed class ImageData {
    data class BitmapData(val bitmap: Bitmap) : ImageData()
    data class StringData(val url: String) : ImageData()
}


public class ImageViewHolder(
    itemView: View,
    listener: onDeleteButtonClickListener?,
    isAbleToDelete: Boolean
) : RecyclerView.ViewHolder(itemView) {
    private val imageView: ImageView = itemView.findViewById(R.id.imageView)
    private val deleteButton: MaterialButton = itemView.findViewById(R.id.deletePhotoButton)
    private var photo: ImageData? = null

    fun bind(photo: ImageData) {
        this.photo = photo

        when (photo) {
            is ImageData.BitmapData -> {
                imageView.setImageBitmap(photo.bitmap)
            }

            is ImageData.StringData -> {
                Picasso.get()
                    .load(photo.url)
                    .into(imageView)
            }
        }
    }

    init {
        if (isAbleToDelete) {
            deleteButton.setOnClickListener {
                listener?.onItemClick(photo)
            }
        } else
            deleteButton.visibility = View.GONE
    }
}