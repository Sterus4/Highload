package ru.sterus.vs.highload.service.impl

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import ru.sterus.vs.highload.exception.ProcessRequestException
import ru.sterus.vs.highload.model.dto.LinkDto
import ru.sterus.vs.highload.model.entity.Link
import ru.sterus.vs.highload.model.entity.LinkId
import ru.sterus.vs.highload.repositories.jpa.LinkJPARepository
import ru.sterus.vs.highload.repositories.jpa.TicketJPARepository
import ru.sterus.vs.highload.repositories.jpa.UserJPARepository
import ru.sterus.vs.highload.service.LinkService
import ru.sterus.vs.highload.service.UserAccessService
import java.util.UUID

@Service
class LinkServiceImpl(
    private val linkJPARepository: LinkJPARepository,
    private val userJPARepository: UserJPARepository,
    private val ticketJPARepository: TicketJPARepository,
    private val userAccessService: UserAccessService,
    ) : LinkService {
    override fun linkTickets(firstTicketId: UUID, secondTicketId: UUID, currentUser: String) {
        val firstTicket = ticketJPARepository.findTicketById(firstTicketId) ?: throw ProcessRequestException(HttpStatus.BAD_REQUEST,"Ticket not found")
        val secondTicket = ticketJPARepository.findTicketById(secondTicketId) ?: throw ProcessRequestException(HttpStatus.BAD_REQUEST,"Ticket not found")
        if (firstTicket.group!!.name != secondTicket.group!!.name) {
            throw ProcessRequestException(HttpStatus.BAD_REQUEST,"Tickets must be in the same group")
        }
        val user = userJPARepository.findUserByName(currentUser) ?: throw ProcessRequestException(HttpStatus.BAD_REQUEST,"User not found")
        userAccessService.assertUserParticipateInGroup(user.id!!, firstTicket.group!!.name!!)

        linkJPARepository.save(Link().apply {
            this.id = LinkId().apply {
                this.firstTicketId = firstTicketId
                this.secondTicketId = secondTicketId
            }
            this.firstTicket = firstTicket
            this.secondTicket = secondTicket
        })
    }

    override fun getLinks(ticketId: UUID): List<LinkDto> {
        ticketJPARepository.findTicketById(ticketId) ?: throw ProcessRequestException(HttpStatus.BAD_REQUEST,"Ticket not found")
        return linkJPARepository.findAllById_FirstTicketIdOrId_SecondTicketId(ticketId, ticketId).map {
            LinkDto(
                firstTicketId = it.id!!.firstTicketId!!,
                secondTicketId = it.id!!.secondTicketId!!
            )
        }
    }

    override fun deleteLink(firstTicketId: UUID, secondTicketId: UUID, currentUser: String) {
        val firstTicket = ticketJPARepository.findTicketById(firstTicketId) ?: throw ProcessRequestException(HttpStatus.BAD_REQUEST,"Ticket not found")
        val secondTicket = ticketJPARepository.findTicketById(secondTicketId) ?: throw ProcessRequestException(HttpStatus.BAD_REQUEST,"Ticket not found")
        val user = userJPARepository.findUserByName(currentUser) ?: throw ProcessRequestException(HttpStatus.BAD_REQUEST,"User not found")
        if (firstTicket.group!!.name != secondTicket.group!!.name) {
            throw ProcessRequestException(HttpStatus.BAD_REQUEST,"Tickets must be in the same group")
        }
        userAccessService.assertUserParticipateInGroup(user.id!!, firstTicket.group!!.name!!)
        linkJPARepository.deleteLinkById_FirstTicketIdOrId_SecondTicketId(firstTicketId, secondTicketId)
        linkJPARepository.deleteLinkById_FirstTicketIdOrId_SecondTicketId(secondTicketId, firstTicketId)
    }
}