package ru.sterus.vs.highload.controllers

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.sterus.vs.highload.model.dto.ticket.CreateTicketDto
import ru.sterus.vs.highload.model.dto.DefaultResponseDto
import ru.sterus.vs.highload.model.dto.ticket.DeleteTicketDto
import ru.sterus.vs.highload.model.dto.ticket.GetTicketDto
import ru.sterus.vs.highload.model.dto.ticket.Ticket
import ru.sterus.vs.highload.model.dto.ticket.UpdateTicketDto
import ru.sterus.vs.highload.service.TicketService
import ru.sterus.vs.highload.util.ServiceUtil
import java.util.UUID

@RequestMapping("/api/ticket")
@RestController
class TicketController(private val ticketService: TicketService, private val serviceUtil: ServiceUtil) {

    @PostMapping("/create")
    fun createTicket(
        @Valid @RequestBody createTicketDto: CreateTicketDto,
        @RequestHeader("Current-User") currentUser: String
    ) : ResponseEntity<DefaultResponseDto>{
        serviceUtil.validateUUID(currentUser)
        val currentUserId = UUID.fromString(currentUser)

        ticketService.createTicket(createTicketDto, currentUserId)
        return ResponseEntity.status(201).body(DefaultResponseDto(message = "Ticket created successfully!"))
    }

    @GetMapping("/get")
    fun getTicket(
        @Valid @RequestBody getTicketDto: GetTicketDto
    ): List<Ticket> {
        return ticketService.getTicket(getTicketDto)
    }

    @PostMapping("/update")
    fun updateTicket(
        @Valid @RequestBody updateTicketDto: UpdateTicketDto,
        @RequestHeader("Current-User") currentUser: String
        ): ResponseEntity<DefaultResponseDto?> {
        serviceUtil.validateUUID(currentUser)
        val currentUserId = UUID.fromString(currentUser)

        ticketService.updateTicket(updateTicketDto, currentUserId)
        return ResponseEntity.ok(DefaultResponseDto(message = "Ticket updated successfully!"))
    }

    @DeleteMapping("/delete")
    fun deleteTicket(
        @Valid @RequestBody deleteTicketDto: DeleteTicketDto,
        @RequestHeader("Current-User") currentUser: String
    ) : ResponseEntity<DefaultResponseDto?> {
        serviceUtil.validateUUID(currentUser)
        val currentUserId = UUID.fromString(currentUser)

        ticketService.deleteTicket(deleteTicketDto, currentUserId)
        return ResponseEntity.ok(DefaultResponseDto(message = "Ticket deleted successfully!"))
    }
}