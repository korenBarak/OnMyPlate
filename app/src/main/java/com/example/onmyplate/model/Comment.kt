package com.example.onmyplate.model

class Comment(
    val userId: String = "",
    val restaurantName: String = "",
    val description: String = "",
    var rating: Float = 0.0f,
){
    fun fromJSON(json: Map<String, Any>): Comment {
        val userId = json["userId"] as? String ?: ""
        val restaurantName = json["restaurantName"] as? String ?: ""
        val description = json["description"] as? String ?: ""
        val rating = json["rating"] as? Float ?: 0.0f

        return Comment(
            userId = userId,
            restaurantName = restaurantName,
            description = description,
            rating = rating
        )
    }
}