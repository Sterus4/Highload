package ru.sterus.vs.highload.model.dto

import jakarta.annotation.Nullable
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateTicketDto(
    @field:NotBlank
    @field:Size(min = 3, max = 255)
    var title: String,

    @field:Nullable
    var description: String,

    @field:NotBlank
    var groupName: String
)
