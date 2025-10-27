package ru.sterus.vs.highload.model.dto.ticket

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import ru.sterus.vs.highload.enums.TicketStatusEnum
import java.util.UUID

data class UpdateTicketDto(
    @field:NotNull
    var id: UUID,
    @field:Size(min = 3, max = 255)
    var title: String?,
    var description: String?,
    var status: TicketStatusEnum?
)