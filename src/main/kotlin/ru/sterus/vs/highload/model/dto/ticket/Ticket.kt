package ru.sterus.vs.highload.model.dto.ticket

import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.validation.constraints.NotNull
import ru.sterus.vs.highload.enums.TicketStatusEnum
import java.time.OffsetDateTime
import java.util.UUID

open class Ticket {
    open var title: String? = null

    open var description: String? = null

    open var ticket_created_at: OffsetDateTime? = null

    open var status: String? = null

    open var author: String? = null

    open var group: String? = null

    companion object {
        fun fromDto(createTicketDto: CreateTicketDto) = Ticket().apply {
            title = createTicketDto.title
            description = createTicketDto.description
        }
    }
}