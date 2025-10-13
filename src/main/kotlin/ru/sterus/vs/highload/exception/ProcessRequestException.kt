package ru.sterus.vs.highload.exception

import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import java.time.Instant

class ProcessRequestException(val code: HttpStatusCode, message: String) : Exception(message) {
    fun getResponse() : ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(code).body(mapOf(
            "timestamp" to Instant.now().toString(),
            "status" to code.toString(),
            "message" to message.toString(),
        ))
    }

}