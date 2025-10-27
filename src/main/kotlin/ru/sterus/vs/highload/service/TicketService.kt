package ru.sterus.vs.highload.service

import ru.sterus.vs.highload.model.dto.CreateTicketDto
import java.util.UUID

interface TicketService {
    fun createTicket(createTicketDto: CreateTicketDto, currentUser: UUID)
}