package ru.sterus.vs.highload.service

import ru.sterus.vs.highload.model.dto.LinkDto
import java.util.UUID

interface LinkService {
    fun linkTickets(firstTicketId: UUID, secondTicketId: UUID, currentUser: String)

    fun getLinks(ticketId: UUID): List<LinkDto>

    fun deleteLink(firstTicketId: UUID, secondTicketId: UUID, currentUser: String)
}