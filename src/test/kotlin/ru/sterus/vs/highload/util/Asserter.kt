package ru.sterus.vs.highload.util

import org.springframework.http.ResponseEntity
import kotlin.test.assertEquals

fun assert200(response: ResponseEntity<*>) {
    assertEquals(200, response.statusCode.value())
}

fun assert201(response: ResponseEntity<*>) {
    assertEquals(201, response.statusCode.value())
}

fun assert400(response: ResponseEntity<*>) {
    assertEquals(400, response.statusCode.value())
}

fun assert403(response: ResponseEntity<*>) {
    assertEquals(403, response.statusCode.value())
}