package ru.sterus.vs.highload.model.dto.ticket

import jakarta.validation.constraints.NotNull
import java.util.UUID

data class DeleteTicketDto(
    @field:NotNull
    val id: UUID
)