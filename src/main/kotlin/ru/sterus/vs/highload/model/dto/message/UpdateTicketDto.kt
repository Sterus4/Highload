package ru.sterus.vs.highload.model.dto.message

import javax.validation.constraints.NotBlank

data class UpdateMessageDto(
    @field:NotBlank
    var text: String,
)