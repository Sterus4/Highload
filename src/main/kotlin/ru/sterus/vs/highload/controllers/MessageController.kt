package ru.sterus.vs.highload.controllers

import jakarta.validation.Valid
import jakarta.websocket.server.PathParam
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.sterus.vs.highload.model.dto.DefaultResponseDto
import ru.sterus.vs.highload.model.dto.MessageDto
import ru.sterus.vs.highload.model.dto.message.CreateMessageDto
import ru.sterus.vs.highload.service.MessageService
import java.util.UUID

@RequestMapping("/api/message")
@RestController
class MessageController(private val messageService: MessageService) {
    @PostMapping("/send/{ticketId}")
    fun createMessage(
        @PathParam("ticketId") ticketId: UUID,
        @Valid @RequestBody createMessageDto: CreateMessageDto,
        @RequestHeader("CurrentUser") currentUser: String
    ) : ResponseEntity<DefaultResponseDto> {
        messageService.createMessage(ticketId, createMessageDto, currentUser)
        return ResponseEntity.status(201).body(DefaultResponseDto(message = "Message created successfully!"))
    }

    @GetMapping("/get/{ticketId}")
    fun getMessage(
        @PathParam("ticketId") ticketId: String,
    ) : ResponseEntity<List<MessageDto>> {
        return ResponseEntity.ok(messageService.getMessage(ticketId))
    }

    @PostMapping("/update/{messageId}")
    fun updateMessage(
        @PathParam("messageId") messageId: Long,
        @Valid @RequestBody updateMessageDto: CreateMessageDto,
        @RequestHeader("CurrentUser") currentUser: String
    ) : ResponseEntity<DefaultResponseDto> {
        messageService.updateMessage(messageId, updateMessageDto, currentUser)
        //TODO 204 кажется
        return ResponseEntity.ok(DefaultResponseDto(message = "Message updated successfully!"))
    }

    @DeleteMapping("/delete/{messageId}")
    fun deleteMessage(
        @PathParam("messageId") messageId: Long,
        @RequestHeader("CurrentUser") currentUser: String
    ) : ResponseEntity<DefaultResponseDto> {
        messageService.deleteMessage(messageId, currentUser)
        return ResponseEntity.status(200).body(DefaultResponseDto(message = "Message deleted successfully!"))
    }
}