package ru.sterus.vs.highload.controllers

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.sterus.vs.highload.model.dto.LinkDto
import ru.sterus.vs.highload.model.dto.DefaultResponseDto
import ru.sterus.vs.highload.service.LinkService
import java.util.UUID

@RequestMapping("/api/link")
@RestController
class LinkController(private val linkService: LinkService) {
    @PostMapping("/create")
    fun createLink(
        @Valid @RequestBody linkDto: LinkDto,
        @RequestHeader("Current-User") currentUser: String
    ) : ResponseEntity<DefaultResponseDto> {
        if (linkDto.firstTicketId == linkDto.secondTicketId) {
            return ResponseEntity.status(400).body(DefaultResponseDto(message = "Cannot link a ticket to itself"))
        }
        linkService.linkTickets(linkDto.firstTicketId, linkDto.secondTicketId, currentUser)
        return ResponseEntity.status(201).body(DefaultResponseDto(message = "Link created successfully!"))
    }

    @GetMapping("/get/{ticketId}")
    fun getLinks(
        @PathVariable("ticketId") ticketId: UUID,
    ) : ResponseEntity<List<LinkDto>> {
        return ResponseEntity.ok(linkService.getLinks(ticketId))
    }

    @PostMapping("/delete")
    fun deleteLink(
        @Valid @RequestBody linkDto: LinkDto,
        @RequestHeader("Current-User") currentUser: String
    ) : ResponseEntity<DefaultResponseDto> {
        linkService.deleteLink(linkDto.firstTicketId, linkDto.secondTicketId, currentUser)
        return ResponseEntity.status(200).body(DefaultResponseDto(message = "Link deleted successfully!"))
    }
}