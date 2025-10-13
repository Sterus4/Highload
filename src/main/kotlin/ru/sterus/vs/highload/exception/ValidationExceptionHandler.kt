// src/main/kotlin/ru/sterus/vs/highload/web/ValidationExceptionHandler.kt
package ru.sterus.vs.highload.exception

import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import java.time.Instant

@RestControllerAdvice
class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, String>> {
        return ResponseEntity.badRequest().body(mapOf(
            "timestamp" to Instant.now().toString(),
            "status" to "400",
            "error" to "Validation Failed",
            "message" to "Field <${ex.fieldError?.field}> ${ex.fieldError?.defaultMessage.toString()}",
            "path" to request.requestURI
        ))
    }

    @ExceptionHandler(ProcessRequestException::class)
    fun handleRequestException(
        ex: ProcessRequestException,
        request: HttpServletRequest,
    ) = ex.getResponse()
}
