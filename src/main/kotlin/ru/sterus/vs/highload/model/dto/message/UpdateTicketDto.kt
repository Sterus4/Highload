package ru.sterus.vs.highload.model.dto.message

import javax.validation.constraints.NotBlank

class UpdateTicketDto(
    @field:NotBlank
    var text: String,
)