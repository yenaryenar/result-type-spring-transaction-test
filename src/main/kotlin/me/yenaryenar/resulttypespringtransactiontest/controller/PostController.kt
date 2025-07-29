package me.yenaryenar.resulttypespringtransactiontest.controller

import me.yenaryenar.resulttypespringtransactiontest.dto.PostCreateRequest
import me.yenaryenar.resulttypespringtransactiontest.dto.PostResponse
import me.yenaryenar.resulttypespringtransactiontest.dto.PostUpdateRequest
import me.yenaryenar.resulttypespringtransactiontest.dto.Result
import me.yenaryenar.resulttypespringtransactiontest.service.PostService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/posts")
class PostController(private val postService: PostService) {

    @PostMapping
    fun createPost(@RequestBody request: PostCreateRequest): ResponseEntity<Result<PostResponse>> {
        val result = postService.createPost(request)
        return if (result.success) {
            ResponseEntity.status(HttpStatus.CREATED).body(result)
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result)
        }
    }

    @GetMapping
    fun getAllPosts(): ResponseEntity<Result<List<PostResponse>>> {
        val result = postService.getAllPosts()
        return if (result.success) {
            ResponseEntity.ok(result)
        } else {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result)
        }
    }

    @GetMapping("/{id}")
    fun getPostById(@PathVariable id: Long): ResponseEntity<Result<PostResponse>> {
        val result = postService.getPostById(id)
        return if (result.success) {
            ResponseEntity.ok(result)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(result)
        }
    }

    @PutMapping("/{id}")
    fun updatePost(
        @PathVariable id: Long,
        @RequestBody request: PostUpdateRequest
    ): ResponseEntity<Result<PostResponse>> {
        val result = postService.updatePost(id, request)
        return if (result.success) {
            ResponseEntity.ok(result)
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result)
        }
    }

    @DeleteMapping("/{id}")
    fun deletePost(@PathVariable id: Long): ResponseEntity<Result<Boolean>> {
        val result = postService.deletePost(id)
        return if (result.success) {
            ResponseEntity.ok(result)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(result)
        }
    }

    @GetMapping("/author/{author}")
    fun findPostsByAuthor(@PathVariable author: String): ResponseEntity<Result<List<PostResponse>>> {
        val result = postService.findPostsByAuthor(author)
        return if (result.success) {
            ResponseEntity.ok(result)
        } else {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result)
        }
    }

    @GetMapping("/search")
    fun searchPostsByTitle(@RequestParam title: String): ResponseEntity<Result<List<PostResponse>>> {
        val result = postService.searchPostsByTitle(title)
        return if (result.success) {
            ResponseEntity.ok(result)
        } else {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result)
        }
    }
}