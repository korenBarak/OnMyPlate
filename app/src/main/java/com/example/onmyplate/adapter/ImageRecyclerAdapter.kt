package com.example.onmyplate.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onmyplate.R
import com.google.android.material.textview.MaterialTextView

class ImageRecyclerAdapter(
    private var imageList: MutableList<Bitmap>,
    private var photoIndicatorTextView: MaterialTextView?
) :
    RecyclerView.Adapter<ImageViewHolder>() {
    var onDeleteListener: onDeleteButtonClickListener? = null

    fun set(imageList: MutableList<Bitmap>) {
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

        setIndicatorTextView(
            holder.itemView.findViewById<MaterialTextView>(R.id.photoIndicatorTextView),
            position + 1
        )
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    fun scrollListener(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                setIndicatorTextView(
                    photoIndicatorTextView,
                    layoutManager.findFirstVisibleItemPosition() + 1
                )

            }
        })
    }

    private fun setIndicatorTextView(textView: MaterialTextView?, currPosition: Int) {
        textView?.text = "${currPosition} / ${imageList.size}"
    }
}

