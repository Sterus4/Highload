package ru.sterus.vs.highload.service.impl

import org.springframework.stereotype.Service
import ru.sterus.vs.highload.model.dto.CreateTicketDto
import ru.sterus.vs.highload.service.TicketService
import java.util.UUID

@Service
class TicketServiceImpl : TicketService {
    override fun createTicket(createTicketDto: CreateTicketDto, currentUser: UUID) {
        TODO("Not yet implemented")
    }
}