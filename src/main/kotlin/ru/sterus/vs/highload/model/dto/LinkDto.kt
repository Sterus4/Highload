package ru.sterus.vs.highload.model.dto

import jakarta.validation.constraints.NotNull
import java.util.UUID

data class LinkDto(
    @field:NotNull
    var firstTicketId: UUID,
    @field:NotNull
    var secondTicketId: UUID
)
