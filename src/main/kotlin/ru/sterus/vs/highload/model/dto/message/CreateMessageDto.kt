package ru.sterus.vs.highload.model.dto.message

import javax.validation.constraints.NotBlank

data class CreateMessageDto(
    @field:NotBlank
    var text: String,
)