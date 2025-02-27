package com.example.onmyplate.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.example.onmyplate.R

class ImageRecyclerAdapter(private var imageList: MutableList<Bitmap>) :
    RecyclerView.Adapter<ImageViewHolder>() {
    var onDeleteListener: onDeleteButtonClickListener? = null

    fun set(imageList:MutableList<Bitmap>) {
        this.imageList = imageList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.single_image_view, parent, false)
        return ImageViewHolder(view, onDeleteListener)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}

