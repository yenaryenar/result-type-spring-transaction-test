package me.yenaryenar.resulttypespringtransactiontest.repository

import me.yenaryenar.resulttypespringtransactiontest.entity.Post
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PostRepository : JpaRepository<Post, Long> {
    // Find posts by author
    fun findByAuthor(author: String): List<Post>
    
    // Find posts containing title (case-insensitive)
    fun findByTitleContainingIgnoreCase(title: String): List<Post>
}