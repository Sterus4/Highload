package ru.sterus.vs.highload.repositories

import org.jooq.DSLContext
import org.jooq.UpdateSetFirstStep
import org.jooq.UpdateSetMoreStep
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import ru.sterus.vs.highload.exception.ProcessRequestException
import ru.sterus.vs.highload.helper.intFromStatus
import ru.sterus.vs.highload.model.dto.ticket.GetTicketDto
import ru.sterus.vs.highload.model.dto.ticket.Ticket
import ru.sterus.vs.highload.model.dto.ticket.UpdateTicketDto
import ru.sterus.vs.highload.repositories.Mapping.*
import java.util.UUID

@Repository
class TicketRepository(private val dsl: DSLContext, private val groupRepository: GroupRepository) {
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

    fun get(getTicketDto: GetTicketDto): List<Ticket> {
        val query = dsl.select(
            TICKET.TITLE.`as`("title"),
            TICKET.DESCRIPTION.`as`("description"),
            TICKET.CREATED_AT.`as`("ticket_created_at"),
            USER_GROUPS.NAME.`as`("group"),
            USERS.NAME.`as`("author"),
            TICKET_STATUS.STATUS.`as`("status")
        ).from(TICKET)
            .join(USER_GROUPS)
            .on(TICKET.GROUP_ID.eq(USER_GROUPS.ID))
            .join(USERS)
            .on(TICKET.AUTHOR_ID.eq(USERS.ID))
            .join(TICKET_STATUS)
            .on(TICKET.STATUS_ID.eq(TICKET_STATUS.ID))

        if (getTicketDto.groupFilter != null) {
            query.where(USER_GROUPS.NAME.`in`(getTicketDto.groupFilter?.groups))
        }
        if (getTicketDto.statusFilter != null) {
            query.where(TICKET.STATUS_ID.eq(getTicketDto.statusFilter!!.status.intFromStatus()))
        }
        query
            .orderBy(TICKET.CREATED_AT.desc())
            .offset(getTicketDto.page.start)
            .limit(getTicketDto.page.size)

        return query.fetchInto(Ticket::class.java)
    }

    fun getOneTicket(id: UUID) : Ticket {
        val query = dsl.select(
            TICKET.TITLE.`as`("title"),
            TICKET.DESCRIPTION.`as`("description"),
            TICKET.CREATED_AT.`as`("ticket_created_at"),
            USER_GROUPS.NAME.`as`("group"),
            USERS.NAME.`as`("author"),
            TICKET_STATUS.STATUS.`as`("status")
        ).from(TICKET)
            .join(USER_GROUPS)
            .on(TICKET.GROUP_ID.eq(USER_GROUPS.ID))
            .join(USERS)
            .on(TICKET.AUTHOR_ID.eq(USERS.ID))
            .join(TICKET_STATUS)
            .on(TICKET.STATUS_ID.eq(TICKET_STATUS.ID))
            .where(TICKET.ID.eq(id))

        return query.fetchInto(Ticket::class.java).single()
    }

    fun updateTicket(updateTicketDto: UpdateTicketDto) {
        var query : UpdateSetMoreStep<*>? = null
        if(updateTicketDto.title != null){
            query = dsl.update(TICKET)
                .set(TICKET.TITLE, updateTicketDto.title)
        }
        if(updateTicketDto.description != null){
            query = (query ?: dsl.update(TICKET))
                .set(TICKET.DESCRIPTION, updateTicketDto.description)
        }
        if(updateTicketDto.status != null){
            query = (query ?: dsl.update(TICKET))
                .set(TICKET.STATUS_ID, updateTicketDto.status!!.intFromStatus())
        }
        query?.where(TICKET.ID.eq(updateTicketDto.id))?.execute()
    }
}