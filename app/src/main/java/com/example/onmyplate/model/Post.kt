package com.example.onmyplate.model

// TODO: add in the future id (uuid, generated by firebase), photoUrl should be string[]..
data class Post(
    val userId: String = "",
    val restaurantName: String = "",
    val tags: String = "",
    val description: String = "",
    var rating: Float = 0.0f,
    val photoUrls: List<String?>? = null
){
    fun fromJSON(json: Map<String, Any>): Post {
        val userId = json["userId"] as? String ?: ""
        val restaurantName = json["restaurantName"] as? String ?: ""
        val tags = json["tags"] as? String ?: ""
        val description = json["description"] as? String ?: ""
        val rating = json["rating"] as? Float ?: 0.0f
        val photoUrls = json["photoUrls"] as? List<String> ?: listOf()

        return Post(
            userId = userId,
            restaurantName = restaurantName,
            tags = tags,
            description = description,
            rating = rating,
            photoUrls = photoUrls
        )
    }
}
