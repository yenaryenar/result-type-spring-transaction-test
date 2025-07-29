
package me.yenaryenar.resulttypespringtransactiontest.service

import me.yenaryenar.resulttypespringtransactiontest.dto.PostCreateRequest
import me.yenaryenar.resulttypespringtransactiontest.dto.PostResponse
import me.yenaryenar.resulttypespringtransactiontest.dto.PostUpdateRequest
import me.yenaryenar.resulttypespringtransactiontest.dto.Result
import me.yenaryenar.resulttypespringtransactiontest.entity.Post
import me.yenaryenar.resulttypespringtransactiontest.repository.PostRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.interceptor.TransactionAspectSupport

@Service
class PostService(private val postRepository: PostRepository) {
    /**
     * Create a new post with transaction support
     * Modified to demonstrate transaction rollback issue
     */
    @Transactional
    fun createPost(request: PostCreateRequest): Result<PostResponse> {
        return runCatching {
            val post = Post(
                title = request.title,
                content = request.content,
                author = request.author
            )

            // 첫 번째 데이터베이스 작업 - 성공
            val savedPost = postRepository.save(post)

            // 강제로 예외 발생 (시뮬레이션)
            if (request.title.contains("FORCE_ERROR")) {
                throw RuntimeException("Forced exception to test transaction rollback")
            }

            Result.success(
                PostResponse.fromEntity(savedPost),
                "Post created successfully"
            )
        }.getOrElse { e ->
            Result.failure("Failed to create post: ${e.message}")
        }
    }

    /**
     * Update post with transaction support
     * Public API with exception handling
     * Uses self-injection to ensure AOP proxy is used for transaction management
     */
    @Transactional
    fun updatePost(id: Long, request: PostUpdateRequest): Result<PostResponse> {
        return runCatching {
            val existingPost = postRepository.findById(id).orElse(null)
                ?: return@runCatching Result.failure("Post not found with ID: $id")

            existingPost.update(request.title, request.content)
            val updatedPost = postRepository.save(existingPost)

            if (request.title.contains("FORCE_ERROR")) {
                // 직접 롤백 마킹 is suck...
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()

                return@runCatching Result.failure("Forced rollback!")
            }

            Result.success(PostResponse.fromEntity(updatedPost), "Success")
        }.getOrElse { e ->
            Result.failure("Failed to update post: ${e.message}")
        }
    }

    /**
     * Get all posts
     */
    @Transactional(readOnly = true)
    fun getAllPosts(): Result<List<PostResponse>> {
        return runCatching {
            val posts = postRepository.findAll()
            val postResponses = posts.map { PostResponse.fromEntity(it) }
            Result.success(postResponses)
        }.getOrElse { e ->
            Result.failure("Failed to retrieve posts: ${e.message}")
        }
    }

    /**
     * Get post by ID
     */
    @Transactional(readOnly = true)
    fun getPostById(id: Long): Result<PostResponse> {
        return runCatching {
            val post = postRepository.findById(id).orElse(null)
                ?: return@runCatching Result.failure("Post not found with ID: $id")

            Result.success(PostResponse.fromEntity(post))
        }.getOrElse { e ->
            Result.failure("Failed to retrieve post: ${e.message}")
        }
    }

    /**
     * Delete post with transaction support
     */
    @Transactional
    fun deletePost(id: Long): Result<Boolean> {
        return runCatching {
            if (!postRepository.existsById(id)) {
                return@runCatching Result.failure("Post not found with ID: $id")
            }

            postRepository.deleteById(id)
            Result.success(true, "Post deleted successfully")
        }.getOrElse { e ->
            Result.failure("Failed to delete post: ${e.message}")
        }
    }

    /**
     * Find posts by author
     */
    @Transactional(readOnly = true)
    fun findPostsByAuthor(author: String): Result<List<PostResponse>> {
        return runCatching {
            val posts = postRepository.findByAuthor(author)
            val postResponses = posts.map { PostResponse.fromEntity(it) }
            Result.success(postResponses)
        }.getOrElse { e ->
            Result.failure("Failed to find posts by author: ${e.message}")
        }
    }

    /**
     * Search posts by title
     */
    @Transactional(readOnly = true)
    fun searchPostsByTitle(title: String): Result<List<PostResponse>> {
        return runCatching {
            val posts = postRepository.findByTitleContainingIgnoreCase(title)
            val postResponses = posts.map { PostResponse.fromEntity(it) }
            Result.success(postResponses)
        }.getOrElse { e ->
            Result.failure("Failed to search posts by title: ${e.message}")
        }
    }
}