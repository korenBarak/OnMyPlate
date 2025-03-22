package com.example.onmyplate.model

class Comment(
    val userId: String = "",
    val postId: String = "",
    val description: String = "",
){
    fun fromJSON(json: Map<String, Any>): Comment {
        val userId = json["userId"] as? String ?: ""
        val postId = json["postId"] as? String ?: ""
        val description = json["description"] as? String ?: ""

        return Comment(
            userId = userId,
            postId = postId,
            description = description,
        )
    }
}