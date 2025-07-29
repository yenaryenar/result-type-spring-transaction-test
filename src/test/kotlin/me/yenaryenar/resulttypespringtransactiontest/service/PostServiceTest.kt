package me.yenaryenar.resulttypespringtransactiontest.service

import me.yenaryenar.resulttypespringtransactiontest.dto.PostCreateRequest
import me.yenaryenar.resulttypespringtransactiontest.dto.PostUpdateRequest
import me.yenaryenar.resulttypespringtransactiontest.entity.Post
import me.yenaryenar.resulttypespringtransactiontest.repository.PostRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest
@DirtiesContext
class PostServiceTest {

    @Autowired
    private lateinit var postService: PostService

    @Autowired
    private lateinit var postRepository: PostRepository

    @Test
    fun `create post should save post and return success result`() {
        // Given
        val request = PostCreateRequest(
            title = "Test Post",
            content = "This is a test post content",
            author = "Test Author"
        )

        // When
        val result = postService.createPost(request)

        // Then
        assertTrue(result.success)
        assertNotNull(result.data)
        assertEquals("Test Post", result.data?.title)
        assertEquals("This is a test post content", result.data?.content)
        assertEquals("Test Author", result.data?.author)
        assertNotNull(result.data?.id)

        // Verify post is saved in the database
        val savedPost = postRepository.findById(result.data?.id!!).orElse(null)
        assertNotNull(savedPost)
        assertEquals("Test Post", savedPost.title)
    }

    @Test
    fun `update post should modify post and return success result`() {
        // Given
        val post = Post(
            title = "Original Title",
            content = "Original Content",
            author = "Original Author"
        )
        val savedPost = postRepository.save(post)

        val updateRequest = PostUpdateRequest(
            title = "FORCE_ERROR",
            content = "Updated Content"
        )

        // When
        val result = postService.updatePost(savedPost.id!!, updateRequest)

        // Then
        assertFalse(result.success)

        // Verify post is not updated in the database
        val updatedPost = postRepository.findById(savedPost.id!!).orElse(null)
        assertNotNull(updatedPost)
        assertNotEquals("FORCE_ERROR", updatedPost.title)
        assertNotEquals("Updated Content", updatedPost.content)
    }

    @Test
    fun `delete post should remove post and return success result`() {
        // Given
        val post = Post(
            title = "Post to Delete",
            content = "This post will be deleted",
            author = "Delete Test Author"
        )
        val savedPost = postRepository.save(post)

        // When
        val result = postService.deletePost(savedPost.id!!)

        // Then
        assertTrue(result.success)
        assertTrue(result.data!!)

        // Verify post is deleted from the database
        val deletedPost = postRepository.findById(savedPost.id!!).orElse(null)
        assertNull(deletedPost)
    }

    @Test
    fun `find posts by author should return matching posts`() {
        // Given
        val author = "Search Author"

        val post1 = Post(
            title = "First Post",
            content = "First post content",
            author = author
        )
        val post2 = Post(
            title = "Second Post",
            content = "Second post content",
            author = author
        )
        val post3 = Post(
            title = "Third Post",
            content = "Third post content",
            author = "Different Author"
        )

        postRepository.saveAll(listOf(post1, post2, post3))

        // When
        val result = postService.findPostsByAuthor(author)

        // Then
        assertTrue(result.success)
        assertNotNull(result.data)
        assertEquals(2, result.data?.size)
        assertTrue(result.data?.all { it.author == author } ?: false)
    }

    @Test
    fun `search posts by title should return matching posts`() {
        // Given
        val titleKeyword = "Search"

        val post1 = Post(
            title = "Search Post One",
            content = "First search post content",
            author = "Author 1"
        )
        val post2 = Post(
            title = "Another Search Post",
            content = "Second search post content",
            author = "Author 2"
        )
        val post3 = Post(
            title = "Unrelated Post",
            content = "Unrelated post content",
            author = "Author 3"
        )

        postRepository.saveAll(listOf(post1, post2, post3))

        // When
        val result = postService.searchPostsByTitle(titleKeyword)

        // Then
        assertTrue(result.success)
        assertNotNull(result.data)
        assertEquals(2, result.data?.size)
        assertTrue(result.data?.all { it.title.contains(titleKeyword, ignoreCase = true) } ?: false)
    }
}