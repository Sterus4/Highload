package ru.sterus.vs.highload.repositories

import org.jooq.DSLContext
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import ru.sterus.vs.highload.exception.ProcessRequestException
import ru.sterus.vs.highload.model.entity.Ticket
import ru.sterus.vs.highload.repositories.Mapping.TICKET
import java.util.UUID

@Repository
class TicketRepository(private val dsl: DSLContext) {
    fun create(ticket: Ticket, authorId: UUID, groupId: UUID) {
        val inserted = dsl.insertInto(Mapping.TICKET)
            .set(TICKET.ID, UUID.randomUUID())
            .set(TICKET.TITLE, ticket.title)
            .set(TICKET.DESCRIPTION, ticket.description)
            .set(TICKET.STATUS_ID, 4)
            .set(TICKET.AUTHOR_ID, authorId)
            .set(TICKET.GROUP_ID, groupId)
            .execute()
        if (inserted != 1) {
            throw ProcessRequestException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create ticket")
        }
    }


}