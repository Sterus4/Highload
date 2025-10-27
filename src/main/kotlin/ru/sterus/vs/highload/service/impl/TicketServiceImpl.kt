package ru.sterus.vs.highload.service.impl

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import ru.sterus.vs.highload.enums.Role
import ru.sterus.vs.highload.exception.ProcessRequestException
import ru.sterus.vs.highload.model.dto.ticket.CreateTicketDto
import ru.sterus.vs.highload.model.dto.ticket.GetTicketDto
import ru.sterus.vs.highload.model.dto.ticket.Ticket
import ru.sterus.vs.highload.repositories.GroupRepository
import ru.sterus.vs.highload.repositories.TicketRepository
import ru.sterus.vs.highload.service.TicketService
import java.util.UUID

@Service
class TicketServiceImpl(val groupRepository: GroupRepository, val ticketRepository: TicketRepository) : TicketService {
    override fun createTicket(createTicketDto: CreateTicketDto, currentUser: UUID) {
        val userGroups = groupRepository.getUserGroups(currentUser).filter {
            it.groupName == createTicketDto.groupName && it.role == Role.ADMIN.toString() || it.groupName == "SUPER"
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
}