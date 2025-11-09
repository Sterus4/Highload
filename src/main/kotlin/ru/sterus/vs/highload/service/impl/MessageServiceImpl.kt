package ru.sterus.vs.highload.service.impl

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import ru.sterus.vs.highload.exception.ProcessRequestException
import ru.sterus.vs.highload.model.dto.MessageDto
import ru.sterus.vs.highload.model.dto.message.CreateMessageDto
import ru.sterus.vs.highload.model.entity.Message
import ru.sterus.vs.highload.repositories.jpa.MessageJPARepository
import ru.sterus.vs.highload.repositories.TicketRepository
import ru.sterus.vs.highload.repositories.jpa.TicketJPARepository
import ru.sterus.vs.highload.repositories.jpa.UserJPARepository
import ru.sterus.vs.highload.service.MessageService
import ru.sterus.vs.highload.service.UserAccessService
import ru.sterus.vs.highload.util.ServiceUtil
import java.time.OffsetDateTime
import java.util.UUID

@Service
class MessageServiceImpl(
    private val messageJPARepository: MessageJPARepository,
    private val ticketRepository: TicketRepository, //TODO можно наверное убрать
    private val ticketJPARepository: TicketJPARepository,
    private val userAccessService: UserAccessService,
    private val userJPARepository: UserJPARepository,
    private val serviceUtil: ServiceUtil
) : MessageService {
    override fun createMessage(ticketId: UUID, createMessageDto: CreateMessageDto, currentUserId: String) {
        val ticket = ticketRepository.getOneTicket(ticketId)
        val currentUser = userJPARepository.findUserByName(currentUserId) ?: throw ProcessRequestException(HttpStatus.BAD_REQUEST,"User not found")
        //TODO if ticket not found
        userAccessService.assertUserParticipateInGroup(currentUser.id!!, ticket!!.group!!)
        val ticketRef = ticketJPARepository.getReferenceById(ticketId)
        val userRef = userJPARepository.getReferenceById(currentUser.id!!)
        messageJPARepository.save(
            Message().apply {
                this.createdBy = userRef
                this.ticket = ticketRef
                this.message = createMessageDto.text
                this.createdAt = OffsetDateTime.now()
            }
        )
    }

    override fun getMessage(ticketId: String): List<MessageDto> {
        serviceUtil.validateUUID(ticketId)
        ticketJPARepository.findTicketById(UUID.fromString(ticketId)) ?: throw ProcessRequestException(HttpStatus.BAD_REQUEST,"Ticket not found")
        return messageJPARepository.findAllByTicketId(UUID.fromString(ticketId)).map {
            MessageDto().apply {
                this.id = it.id
                this.ticketId = UUID.fromString(ticketId)
                this.message = it.message
                this.createdAt = it.createdAt
                this.createdBy = it.createdBy!!.name
            }
        }
    }

    override fun updateMessage(messageId: Long, updateMessageDto: CreateMessageDto, currentUser: String) {
        userJPARepository.findUserByName(currentUser) ?: throw ProcessRequestException(HttpStatus.BAD_REQUEST,"User not found")
        val message = messageJPARepository.findMessageById(messageId) ?: throw ProcessRequestException(HttpStatus.BAD_REQUEST,"Message not found")
        if(message.createdBy!!.name != currentUser) throw ProcessRequestException(HttpStatus.FORBIDDEN,"You can't update this message")
        message.message = updateMessageDto.text
        messageJPARepository.save(message)
    }

    override fun deleteMessage(messageId: Long, currentUser: String) {
        userJPARepository.findUserByName(currentUser) ?: throw ProcessRequestException(HttpStatus.BAD_REQUEST,"User not found")
        val message = messageJPARepository.findMessageById(messageId) ?: throw ProcessRequestException(HttpStatus.BAD_REQUEST,"Message not found")
        if(message.createdBy!!.name != currentUser) throw ProcessRequestException(HttpStatus.FORBIDDEN,"You can't delete this message")
        messageJPARepository.deleteMessageById(messageId)
    }
}