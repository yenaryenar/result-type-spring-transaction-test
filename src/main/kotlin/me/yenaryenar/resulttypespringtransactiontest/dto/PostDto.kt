package me.yenaryenar.resulttypespringtransactiontest.dto

import me.yenaryenar.resulttypespringtransactiontest.entity.Post
import java.time.LocalDateTime

/**
 * DTO for creating a new post
 */
data class PostCreateRequest(
    val title: String,
    val content: String,
    val author: String
)

/**
 * DTO for updating an existing post
 */
data class PostUpdateRequest(
    val title: String,
    val content: String
)

/**
 * DTO for post response
 */
data class PostResponse(
    val id: Long?,
    val title: String,
    val content: String,
    val author: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(post: Post): PostResponse {
            return PostResponse(
                id = post.id,
                title = post.title,
                content = post.content,
                author = post.author,
                createdAt = post.createdAt,
                updatedAt = post.updatedAt
            )
        }
    }
}