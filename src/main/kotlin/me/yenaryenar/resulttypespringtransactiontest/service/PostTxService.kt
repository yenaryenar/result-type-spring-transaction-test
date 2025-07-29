package me.yenaryenar.resulttypespringtransactiontest.service

import me.yenaryenar.resulttypespringtransactiontest.dto.PostResponse
import me.yenaryenar.resulttypespringtransactiontest.dto.PostUpdateRequest
import me.yenaryenar.resulttypespringtransactiontest.dto.Result
import me.yenaryenar.resulttypespringtransactiontest.repository.PostRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

// 프록시 패턴으로 우회 롤백 is suck...
@Service
class PostTxService(private val postRepository: PostRepository) {
    @Transactional
    fun updatePostTransaction(
        id: Long,
        request: PostUpdateRequest
    ): me.yenaryenar.resulttypespringtransactiontest.dto.Result<PostResponse> {
        val existingPost = postRepository.findById(id).orElse(null)
            ?: return Result.failure("Post not found with ID: $id")

        existingPost.update(request.title, request.content)
        val updatedPost = postRepository.save(existingPost)

        if (request.title.contains("FORCE_ERROR")) {
            // 직접 롤백 마킹 is suck...
            //TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()

            throw RuntimeException("Forced rollback!")
        }

        return Result.success(PostResponse.fromEntity(updatedPost), "Success")
    }

}