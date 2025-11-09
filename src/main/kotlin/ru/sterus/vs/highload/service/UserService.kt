package ru.sterus.vs.highload.service

import ru.sterus.vs.highload.model.dto.GroupDto
import ru.sterus.vs.highload.model.dto.UserDto
import ru.sterus.vs.highload.model.dto.UserRoleGroup
import java.util.UUID

interface UserService {
    fun registerUser(user: UserDto)

    fun getUser(name: String): UUID

    fun getUserGroups(name: String): List<UserRoleGroup>
}