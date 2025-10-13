package ru.sterus.vs.highload.controllers

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.sterus.vs.highload.model.entity.UserDto
import ru.sterus.vs.highload.openapi.model.ApiUserRegisterPost200ResponseDto
import ru.sterus.vs.highload.service.UserService

@RestController
class UserController(private val userService: UserService, service: UserService){

    @PostMapping("/api/user/register")
    fun register(@Valid @RequestBody request: UserDto): ResponseEntity<ApiUserRegisterPost200ResponseDto>{
        userService.registerUser(request)
        return ResponseEntity.ok(ApiUserRegisterPost200ResponseDto().message(
            "User ${request.name} registered successfully"
        ))
    }
}