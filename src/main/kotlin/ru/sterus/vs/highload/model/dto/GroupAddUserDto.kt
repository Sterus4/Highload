package ru.sterus.vs.highload.model.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import ru.sterus.vs.highload.enums.Role

data class GroupAddUserDto(
    @field:NotNull
    @field:Valid
    var group: GroupDto,
    @field:NotNull
    var username: String,
    @field:NotNull
    var role: Role
)