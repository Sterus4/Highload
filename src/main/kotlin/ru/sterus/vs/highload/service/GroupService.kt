package ru.sterus.vs.highload.service

import ru.sterus.vs.highload.model.dto.GroupDto
import ru.sterus.vs.highload.model.dto.Role
import java.util.UUID

interface GroupService {
    fun createGroup(group: GroupDto, currentUser: UUID)

    fun addUser(group: GroupDto, currentUser: UUID, userToAdd: String, role: Role)
}