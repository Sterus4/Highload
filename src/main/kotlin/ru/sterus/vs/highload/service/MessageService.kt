package ru.sterus.vs.highload.service

import ru.sterus.vs.highload.model.dto.MessageDto
import ru.sterus.vs.highload.model.dto.message.CreateMessageDto
import java.util.UUID

interface MessageService {
    fun createMessage(ticketId: UUID, createMessageDto: CreateMessageDto, currentUserId: String)
    fun getMessage(ticketId: String): List<MessageDto>
    fun updateMessage(messageId: Long, updateMessageDto: CreateMessageDto, currentUser: String)
    fun deleteMessage(messageId: Long, currentUser: String)
}