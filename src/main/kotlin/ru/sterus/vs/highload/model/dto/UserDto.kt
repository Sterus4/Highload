package ru.sterus.vs.highload.model.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class UserDto (
    @field:NotNull
    @field:NotBlank
    @field:Size(min = 4, max = 50)
    var name: String
)