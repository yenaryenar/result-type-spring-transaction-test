package me.yenaryenar.resulttypespringtransactiontest.dto

/**
 * Generic Result class to wrap responses
 */
data class Result<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
) {
    companion object {
        fun <T> success(data: T): Result<T> {
            return Result(true, data, null)
        }

        fun <T> success(data: T, message: String): Result<T> {
            return Result(true, data, message)
        }

        fun <T> failure(message: String): Result<T> {
            return Result(false, null, message)
        }
    }
}