package ru.sterus.vs.highload.model.dto

import jakarta.validation.constraints.NotNull

data class UserRoleGroup(
    @field:NotNull
    var groupName: String,
    var role: String,
    @field:NotNull
    var userName: String,
)