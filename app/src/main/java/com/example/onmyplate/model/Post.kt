package com.example.onmyplate.model

data class Post(
    val postId: String? = null,
    val userId: String = "",
    val restaurantName: String = "",
    val tags: String = "",
    val description: String = "",
    var rating: Float = 0.0f,
    val photoUrls: List<String?>? = null,
    val googleRating: Double? = 0.0
) {

    companion object {
        fun fromJSON(json: Map<String, Any>): Post {
            val userId = json["userId"] as? String ?: ""
            val postId = json["postId"] as? String ?: ""
            val restaurantName = json["restaurantName"] as? String ?: ""
            val tags = json["tags"] as? String ?: ""
            val description = json["description"] as? String ?: ""
            val rating = (json["rating"] as? Double ?: 0.0).toFloat()
            val googleRating = json["googleRating"] as? Double ?: 0.0
            val photoUrls = json["photoUrls"] as? List<String> ?: listOf()

            return Post(
                postId = postId,
                userId = userId,
                restaurantName = restaurantName,
                tags = tags,
                description = description,
                rating = rating,
                photoUrls = photoUrls,
                googleRating = googleRating
            )
        }

        fun toMap(post: Post): Map<String, Any> {
            return mapOf(
                "restaurantName" to post.restaurantName,
                "tags" to post.tags,
                "description" to post.description,
                "rating" to post.rating,
                "photoUrls" to (post.photoUrls ?: listOf()),
                "googleRating" to (post.googleRating ?: 0.0)
            )
        }
    }
}
