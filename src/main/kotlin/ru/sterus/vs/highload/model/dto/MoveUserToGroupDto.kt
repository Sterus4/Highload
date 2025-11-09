package ru.sterus.vs.highload.model.dto

import jakarta.validation.constraints.NotNull
import ru.sterus.vs.highload.enums.Role
import java.util.UUID

data class MoveUserToGroupDto(
    @field:NotNull
    var userId: UUID,
    @field:NotNull
    var groupId: UUID,
    @field:NotNull
    var role: Role
)
