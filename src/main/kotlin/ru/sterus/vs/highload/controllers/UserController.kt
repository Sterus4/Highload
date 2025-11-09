package ru.sterus.vs.highload.controllers

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.sterus.vs.highload.model.dto.DefaultResponseDto
import ru.sterus.vs.highload.model.dto.UserDto
import ru.sterus.vs.highload.model.dto.UserRoleGroup
import ru.sterus.vs.highload.service.UserService
import java.util.UUID

@RestController
class UserController(private val userService: UserService){

    @PostMapping("/api/user/register")
    fun register(@Valid @RequestBody request: UserDto): ResponseEntity<DefaultResponseDto>{
        userService.registerUser(request)
        return ResponseEntity.status(201).body(DefaultResponseDto(message =
            "User ${request.name} registered successfully"
        ))
    }

    @GetMapping("/api/user/get/{name}")
    fun getUser(
        @PathVariable("name") name: String
    ): ResponseEntity<UUID>{
        val userId = userService.getUser(name)
        return ResponseEntity.ok(userId)
    }

    @GetMapping("/api/user/get/{name}/groups")
    fun getUserGroups(
        @PathVariable("name") name: String
    ): ResponseEntity<List<UserRoleGroup>> {
        val userId = userService.getUserGroups(name)
        return ResponseEntity.ok(userId)
    }
}