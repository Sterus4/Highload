package ru.sterus.vs.highload.util

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import ru.sterus.vs.highload.exception.ProcessRequestException
import java.util.UUID

@Component
class ServiceUtil {
    fun validateUUID(s: String){
      try {
          UUID.fromString(s)
      } catch (_: IllegalArgumentException){
          throw ProcessRequestException(HttpStatus.BAD_REQUEST, "Not valid UUID: $s")
      }
    }
}