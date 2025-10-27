package ru.sterus.vs.highload.controllers

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import ru.sterus.vs.highload.model.dto.ticket.CreateTicketDto
import ru.sterus.vs.highload.model.dto.DefaultResponseDto
import ru.sterus.vs.highload.model.dto.ticket.DeleteTicketDto
import ru.sterus.vs.highload.model.dto.ticket.GetTicketDto
import ru.sterus.vs.highload.model.dto.ticket.UpdateTicketDto
import ru.sterus.vs.highload.service.TicketService
import ru.sterus.vs.highload.util.ServiceUtil
import java.util.UUID

@RestController
class TicketController(private val ticketService: TicketService, private val serviceUtil: ServiceUtil) {

    @PostMapping("/api/ticket/create")
    fun createTicket(
        @Valid @RequestBody createTicketDto: CreateTicketDto,
        @RequestHeader("CurrentUser") currentUser: String) : ResponseEntity<DefaultResponseDto>{
        serviceUtil.validateUUID(currentUser)
        val currentUserId = UUID.fromString(currentUser)

        ticketService.createTicket(createTicketDto, currentUserId)
        return ResponseEntity.ok(DefaultResponseDto(message = "Ticket created successfully!"))
    }

    @GetMapping("/api/ticket/get")
    fun getTicket(
        @Valid @RequestBody getTicketDto: GetTicketDto
    ) = ticketService.getTicket(getTicketDto)

    @PostMapping("/api/ticket/update")
    fun updateTicket(
        @Valid @RequestBody updateTicketDto: UpdateTicketDto,
        @RequestHeader("CurrentUser") currentUser: String
        ): ResponseEntity<DefaultResponseDto?> {
        serviceUtil.validateUUID(currentUser)
        val currentUserId = UUID.fromString(currentUser)

        ticketService.updateTicket(updateTicketDto, currentUserId)
        return ResponseEntity.ok(DefaultResponseDto(message = "Ticket updated successfully!"))
    }

    @DeleteMapping("/api/ticket/delete")
    fun deleteTicket(
        @Valid @RequestBody deleteTicketDto: DeleteTicketDto,
        @RequestHeader("CurrentUser") currentUser: String
    ) : ResponseEntity<DefaultResponseDto?> {
        serviceUtil.validateUUID(currentUser)
        val currentUserId = UUID.fromString(currentUser)

        ticketService.deleteTicket(deleteTicketDto, currentUserId)
        return ResponseEntity.ok(DefaultResponseDto(message = "Ticket deleted successfully!"))
    }
}