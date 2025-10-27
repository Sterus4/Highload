package ru.sterus.vs.highload.service

import ru.sterus.vs.highload.model.dto.ticket.CreateTicketDto
import ru.sterus.vs.highload.model.dto.ticket.GetTicketDto
import ru.sterus.vs.highload.model.dto.ticket.Ticket
import java.util.UUID

interface TicketService {
    fun createTicket(createTicketDto: CreateTicketDto, currentUser: UUID)

    fun getTicket(getTicketDto: GetTicketDto): List<Ticket>
}