package ru.sterus.vs.highload.controllers

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import ru.sterus.vs.highload.model.dto.CreateTicketDto
import ru.sterus.vs.highload.model.dto.DefaultResponseDto
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

}