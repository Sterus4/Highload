package ru.sterus.vs.highload.controllers

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import ru.sterus.vs.highload.model.dto.DefaultResponseDto
import ru.sterus.vs.highload.model.dto.GroupAddUserDto
import ru.sterus.vs.highload.model.dto.GroupDto
import ru.sterus.vs.highload.service.GroupService
import ru.sterus.vs.highload.util.ServiceUtil
import java.util.UUID

@RestController
class GroupController(private val groupService: GroupService, private val serviceUtil: ServiceUtil) {
        @PostMapping("/api/group/create")
    fun createGroup(
        @RequestHeader("CurrentUser") currentUser: String,
        @Valid @RequestBody group: GroupDto
    ) : ResponseEntity<DefaultResponseDto>{
        serviceUtil.validateUUID(currentUser)
        val currentUserId = UUID.fromString(currentUser)

        groupService.createGroup(group, currentUserId)
        //TODO 201
        return ResponseEntity.ok(DefaultResponseDto(message=
            "Group <${group.name}> created successfully!",
        ))
    }

    @PostMapping("/api/group/addUser")
    fun addUserGroup(
        @RequestHeader("CurrentUser") currentUser: String,
        @Valid @RequestBody request: GroupAddUserDto
    ) : ResponseEntity<DefaultResponseDto>{
        serviceUtil.validateUUID(currentUser)
        val currentUserId = UUID.fromString(currentUser)

        groupService.addUser(request.group, currentUserId, request.username, request.role)
        return ResponseEntity.ok(DefaultResponseDto(message = ("User added successfully!")))
    }



}