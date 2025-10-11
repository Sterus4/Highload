package ru.sterus.vs.highload.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {
    @GetMapping("/echo/{message}")
    fun echo(@PathVariable message: String): Map<String, String> {
        return mapOf("message" to message)
    }
}