package ru.sterus.vs.highload.e2e.util

import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import ru.sterus.vs.highload.model.dto.DefaultResponseDto
import ru.sterus.vs.highload.model.dto.GroupAddUserDto
import ru.sterus.vs.highload.model.dto.GroupDto
import ru.sterus.vs.highload.model.dto.LinkDto
import ru.sterus.vs.highload.model.dto.MessageDto
import ru.sterus.vs.highload.model.dto.UserDto
import ru.sterus.vs.highload.model.dto.UserRoleGroup
import ru.sterus.vs.highload.model.dto.message.CreateMessageDto
import ru.sterus.vs.highload.model.dto.message.UpdateMessageDto
import ru.sterus.vs.highload.model.dto.ticket.CreateTicketDto
import ru.sterus.vs.highload.model.dto.ticket.DeleteTicketDto
import ru.sterus.vs.highload.model.dto.ticket.GetTicketDto
import ru.sterus.vs.highload.model.dto.ticket.Ticket
import ru.sterus.vs.highload.model.dto.ticket.UpdateTicketDto
import java.util.UUID
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator

val mapper = jacksonObjectMapper()

fun <T> TestRestTemplate.send(
    method: HttpMethod,
    requestBody: Map<String, Any>?,
    url: String,
    responseType: Class<T>,
    h: Map<String, String>? = null
): ResponseEntity<T?> {
    val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
    if (h != null) {
        for ((key, value) in h) {
            headers.apply { this[key] = value }
        }
    }
    val requestEntity = HttpEntity(requestBody, headers)

    return this.exchange(url, method, requestEntity, responseType)
}


inline fun <reified T> TestRestTemplate.sendReceiveList(
    method: HttpMethod,
    requestBody: Map<String, Any>?,
    url: String,
    h: Map<String, String>? = null
): ResponseEntity<T> {
    val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
    if (h != null) {
        for ((key, value) in h) {
            headers.apply { this[key] = value }
        }
    }
    val requestEntity = HttpEntity(requestBody, headers)
    return this.exchange(url, method, requestEntity, object : ParameterizedTypeReference<T>() {})
}

fun TestRestTemplate.registerUser(port: Int, userDto: UserDto) = this.send(
    HttpMethod.POST,
    mapper.convertValue(userDto),
    "http://localhost:$port/api/user/register",
    DefaultResponseDto::class.java
)

fun TestRestTemplate.getUser(port: Int, name: String) = this.send(
    HttpMethod.GET,
    null,
    "http://localhost:$port/api/user/get/$name",
    UUID::class.java
)

fun TestRestTemplate.getUserGroups(port: Int, name: String) = this.sendReceiveList<List<UserRoleGroup>>(
    HttpMethod.GET,
    null,
    "http://localhost:$port/api/user/get/$name/groups"
)


fun TestRestTemplate.createGroup(port: Int, GroupDto: GroupDto) = this.send(
    HttpMethod.POST,
    mapper.convertValue(GroupDto),
    "http://localhost:$port/api/group/create",
    DefaultResponseDto::class.java,
    mapOf("Current-User" to "73cf6c42-69d8-4b4b-9379-beb93c916a36")
)

fun TestRestTemplate.addUserGroup(port: Int, groupAddUserDto: GroupAddUserDto) = this.send(
    HttpMethod.POST,
    mapper.convertValue(groupAddUserDto),
    "http://localhost:$port/api/group/user/add",
    DefaultResponseDto::class.java,
    mapOf("Current-User" to "73cf6c42-69d8-4b4b-9379-beb93c916a36")
)

fun TestRestTemplate.createTicket(port: Int, currentUserId: UUID, request: CreateTicketDto) = this.send(
    HttpMethod.POST,
    mapper.convertValue(request),
    "http://localhost:$port/api/ticket/create",
    DefaultResponseDto::class.java,
    mapOf("Current-User" to currentUserId.toString())
)

fun TestRestTemplate.getTicket(port: Int, ticketDto: GetTicketDto) = this.sendReceiveList<List<Ticket>>(
    HttpMethod.GET,
    mapper.convertValue(ticketDto),
    "http://localhost:$port/api/ticket/get",
)

fun TestRestTemplate.updateTicket(port: Int, currentUserId: UUID, updateTicketDto: UpdateTicketDto) = this.send(
    HttpMethod.POST,
    mapper.convertValue(updateTicketDto),
    "http://localhost:$port/api/ticket/update",
    DefaultResponseDto::class.java,
    mapOf("Current-User" to currentUserId.toString())
)

fun TestRestTemplate.deleteTicket(port: Int, currentUserId: UUID, deleteTicketDto: DeleteTicketDto) = this.send(
    HttpMethod.DELETE,
    mapper.convertValue(deleteTicketDto),
    "http://localhost:$port/api/ticket/delete",
    DefaultResponseDto::class.java,
    mapOf("Current-User" to currentUserId.toString())
)

fun TestRestTemplate.createMessage(port: Int, currentUserId: String, createMessageDto: CreateMessageDto, ticketId: UUID) = this.send(
    HttpMethod.POST,
    mapper.convertValue(createMessageDto),
    "http://localhost:$port/api/message/send/$ticketId",
    DefaultResponseDto::class.java,
    mapOf("Current-User" to currentUserId)
)

fun TestRestTemplate.getMessage(port: Int, ticketId: UUID) = this.sendReceiveList<List<MessageDto>>(
    HttpMethod.GET,
    null,
    "http://localhost:$port/api/message/get/$ticketId",
)

fun TestRestTemplate.updateMessage(port: Int, currentUserId: String, updateMessageDto: UpdateMessageDto, messageId: Long) = this.send(
    HttpMethod.POST,
    mapper.convertValue(updateMessageDto),
    "http://localhost:$port/api/message/update/$messageId",
    DefaultResponseDto::class.java,
    mapOf("Current-User" to currentUserId)
)

fun TestRestTemplate.deleteMessage(port: Int, currentUserId: String, messageId: Long) = this.send(
    HttpMethod.DELETE,
    null,
    "http://localhost:$port/api/message/delete/$messageId",
    DefaultResponseDto::class.java,
    mapOf("Current-User" to currentUserId)
)

fun TestRestTemplate.createLink(port: Int, currentUserId: String, createLinkDto: LinkDto) = this.send(
    HttpMethod.POST,
    mapper.convertValue(createLinkDto),
    "http://localhost:$port/api/link/create",
    DefaultResponseDto::class.java,
    mapOf("Current-User" to currentUserId)
)

fun TestRestTemplate.getLinks(port: Int, ticketId: UUID) = this.sendReceiveList<List<LinkDto>>(
    HttpMethod.GET,
    null,
    "http://localhost:$port/api/link/get/$ticketId",
)