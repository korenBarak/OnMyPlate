package com.example.onmyplate.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PartialPost(
    @PrimaryKey val id: String, val restaurantName: String, val description: String,
    val tags: String,
    val rating: Float
) {
}