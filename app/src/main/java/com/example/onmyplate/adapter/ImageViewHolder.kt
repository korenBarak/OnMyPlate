package com.example.onmyplate.adapter;

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.onmyplate.R

interface onDeleteButtonClickListener {
    fun onItemClick(photo: Bitmap?)
}

public class ImageViewHolder (itemView: View, listener: onDeleteButtonClickListener?) : RecyclerView.ViewHolder(itemView) {
    private val imageView: ImageView = itemView.findViewById(R.id.imageView)
    private val deleteButton: ImageView = itemView.findViewById(R.id.deletePhotoButton)
    private var photo: Bitmap? = null

    fun bind(photo: Bitmap) {
        this.photo = photo

        imageView.setImageBitmap(photo)
    }

    init {
      deleteButton.setOnClickListener {
            listener?.onItemClick(photo)
      }
    }
}