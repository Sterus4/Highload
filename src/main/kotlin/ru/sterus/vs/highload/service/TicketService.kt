package ru.sterus.vs.highload.service

import ru.sterus.vs.highload.model.dto.ticket.CreateTicketDto
import ru.sterus.vs.highload.model.dto.ticket.DeleteTicketDto
import ru.sterus.vs.highload.model.dto.ticket.GetTicketDto
import ru.sterus.vs.highload.model.dto.ticket.Ticket
import ru.sterus.vs.highload.model.dto.ticket.UpdateTicketDto
import java.util.UUID

interface TicketService {
    fun createTicket(createTicketDto: CreateTicketDto, currentUser: UUID)

    fun getTicket(getTicketDto: GetTicketDto): List<Ticket>
    fun updateTicket(updateTicketDto: UpdateTicketDto, currentUserId: UUID)
    fun deleteTicket(deleteTicketDto: DeleteTicketDto, currentUserId: UUID)
}