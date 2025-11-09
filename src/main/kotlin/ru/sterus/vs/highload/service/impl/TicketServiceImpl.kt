package ru.sterus.vs.highload.service.impl

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import ru.sterus.vs.highload.exception.ProcessRequestException
import ru.sterus.vs.highload.model.dto.ticket.CreateTicketDto
import ru.sterus.vs.highload.model.dto.ticket.DeleteTicketDto
import ru.sterus.vs.highload.model.dto.ticket.GetTicketDto
import ru.sterus.vs.highload.model.dto.ticket.Ticket
import ru.sterus.vs.highload.model.dto.ticket.UpdateTicketDto
import ru.sterus.vs.highload.repositories.GroupRepository
import ru.sterus.vs.highload.repositories.TicketRepository
import ru.sterus.vs.highload.service.TicketService
import java.util.UUID

@Service
class TicketServiceImpl(val groupRepository: GroupRepository, val ticketRepository: TicketRepository) : TicketService {
    override fun createTicket(createTicketDto: CreateTicketDto, currentUser: UUID) {
        val userGroups = groupRepository.getUserGroups(currentUser).filter {
            it.groupName == createTicketDto.groupName || it.groupName == "SUPER"
        }
        if (userGroups.isEmpty()) {
            throw ProcessRequestException(HttpStatus.FORBIDDEN, "You are not allowed to manage group <${createTicketDto.groupName}>")
        }

        val groupId = groupRepository.getGroupByName(createTicketDto.groupName).singleOrNull()
            ?: throw ProcessRequestException(HttpStatus.BAD_REQUEST, "Group <${createTicketDto.groupName}> does not exist")
        ticketRepository.create(Ticket.fromDto(createTicketDto), currentUser, groupId.id!!)
    }

    override fun getTicket(getTicketDto: GetTicketDto): List<Ticket>
        = ticketRepository.get(getTicketDto)

    override fun updateTicket(updateTicketDto: UpdateTicketDto, currentUserId: UUID) {
        val currentTicket = ticketRepository.getOneTicket(updateTicketDto.id) ?: throw ProcessRequestException(
            HttpStatus.BAD_REQUEST,
            "Ticket <${updateTicketDto.id}> does not exist"
        )
        // TODO Обновлять только создатель
        val userGroups = groupRepository.getUserGroups(currentUserId)
        val accesses = userGroups.filter {
            it.groupName == currentTicket.group || it.groupName == "SUPER"
        }
        if (accesses.isEmpty()) {
            throw ProcessRequestException(HttpStatus.FORBIDDEN, "You are not allowed to manage ticket <${updateTicketDto.id}>")
        }

        ticketRepository.updateTicket(updateTicketDto)
    }

    override fun deleteTicket(deleteTicketDto: DeleteTicketDto, currentUserId: UUID) {
        val userGroups = groupRepository.getUserGroups(currentUserId)
        val currentTicket = ticketRepository.getOneTicket(deleteTicketDto.id) ?: throw ProcessRequestException(
            HttpStatus.BAD_REQUEST,
            "Ticket <${deleteTicketDto.id}> does not exist"
        )
        // TODO удалять только создатель
        val accesses = userGroups.filter {
            it.groupName == currentTicket.group || it.groupName == "SUPER"
        }

        if (accesses.isEmpty()) {
            throw ProcessRequestException(HttpStatus.FORBIDDEN, "You are not allowed to manage ticket <${deleteTicketDto.id}>")
        }

        ticketRepository.deleteTicket(deleteTicketDto)
    }
}