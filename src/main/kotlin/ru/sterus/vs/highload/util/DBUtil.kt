package ru.sterus.vs.highload.util

import jakarta.validation.Validator
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import ru.sterus.vs.highload.exception.ProcessRequestException

@Component
class DBUtil(private val validator: Validator) {
    fun validate(o: Any) {
        val errors = validator.validate(o)
        if (errors.isNotEmpty()) {
            val errorMessage = errors.joinToString {
                "${it.propertyPath}: ${it.message}"
            }
            throw ProcessRequestException(HttpStatus.BAD_REQUEST, errorMessage)
        }
    }
}