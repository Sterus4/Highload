package ru.sterus.vs.highload.e2e

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.jdbc.Sql
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import ru.sterus.vs.highload.e2e.util.addUserGroup
import ru.sterus.vs.highload.e2e.util.createGroup
import ru.sterus.vs.highload.e2e.util.createMessage
import ru.sterus.vs.highload.e2e.util.createTicket
import ru.sterus.vs.highload.e2e.util.deleteMessage
import ru.sterus.vs.highload.e2e.util.getMessage
import ru.sterus.vs.highload.e2e.util.getTicket
import ru.sterus.vs.highload.e2e.util.getUser
import ru.sterus.vs.highload.e2e.util.registerUser
import ru.sterus.vs.highload.e2e.util.updateMessage
import ru.sterus.vs.highload.enums.Role
import ru.sterus.vs.highload.model.dto.GroupAddUserDto
import ru.sterus.vs.highload.model.dto.GroupDto
import ru.sterus.vs.highload.model.dto.UserDto
import ru.sterus.vs.highload.model.dto.message.CreateMessageDto
import ru.sterus.vs.highload.model.dto.message.UpdateMessageDto
import ru.sterus.vs.highload.model.dto.ticket.CreateTicketDto
import ru.sterus.vs.highload.model.dto.ticket.GetTicketDto
import ru.sterus.vs.highload.model.dto.ticket.GroupFilter
import ru.sterus.vs.highload.model.dto.ticket.Page
import ru.sterus.vs.highload.util.assert200
import ru.sterus.vs.highload.util.assert201
import kotlin.test.Test
import kotlin.test.assertTrue

@Sql("/sql/init.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class MessageScenario {
    companion object {
        @Container
        private val postgres = PostgreSQLContainer<Nothing>("postgres:16").apply {
            withDatabaseName("highload")
            withUsername("user")
            withPassword("password")
        }

        @JvmStatic
        @DynamicPropertySource
        fun configDataSource(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }

    @LocalServerPort
    var port: Int = 0

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun `should create message`() {
        val registerResponse = restTemplate.registerUser(port, UserDto("sterus1"))
        assert201(registerResponse)

        val userId = restTemplate.getUser(port, "sterus1")
        assert200(userId)

        val createGroup = restTemplate.createGroup(port, GroupDto("my-first-group"))
        assert201(createGroup)

        val addUserResponse = restTemplate.addUserGroup(port, GroupAddUserDto(GroupDto("my-first-group"), "sterus1", Role.ADMIN))
        assert200(addUserResponse)

        val createTicket = restTemplate.createTicket(port, userId.body!!, CreateTicketDto(
            "my-first-ticket",
            "my-first-description",
            "my-first-group",
        ))
        assert201(createTicket)

        val getTicket = restTemplate.getTicket(port, GetTicketDto(
            page = Page(0, 50),
            groupFilter = GroupFilter(
                groups = listOf("my-first-group")
            )
        ))
        assert200(getTicket)

        val createdTicket = getTicket.body!!.single()

        val createMessage = restTemplate.createMessage(port, "sterus1", CreateMessageDto(
            "lol"
        ), createdTicket.id!!)
        assert201(createMessage)

        val messages = restTemplate.getMessage(port, createdTicket.id!!)
        assert200(messages)

        assertTrue { messages.body!!.size == 1 }
        assertTrue { messages.body!!.first().message == "lol" }

        restTemplate.updateMessage(port, "sterus1", UpdateMessageDto("lol2"), messages.body!!.first().id!!)
        val updatedMessages = restTemplate.getMessage(port, createdTicket.id!!)
        assert200(updatedMessages)
        assertTrue { updatedMessages.body!!.size == 1 }
        assertTrue { updatedMessages.body!!.first().message == "lol2" }
    }

    @Test
    fun `should delete my message`(){
        val registerResponse = restTemplate.registerUser(port, UserDto("sterus2"))
        assert201(registerResponse)

        val userId = restTemplate.getUser(port, "sterus2")
        assert200(userId)

        val createGroup = restTemplate.createGroup(port, GroupDto("my-second-group"))
        assert201(createGroup)

        val addUserResponse = restTemplate.addUserGroup(port, GroupAddUserDto(GroupDto("my-second-group"), "sterus2", Role.ADMIN))
        assert200(addUserResponse)

        val createTicket = restTemplate.createTicket(port, userId.body!!, CreateTicketDto(
            "my-first-ticket",
            "my-first-description",
            "my-second-group",
        ))
        assert201(createTicket)

        val getTicket = restTemplate.getTicket(port, GetTicketDto(
            page = Page(0, 50),
            groupFilter = GroupFilter(
                groups = listOf("my-second-group")
            )
        ))
        assert200(getTicket)

        val createdTicket = getTicket.body!!.single()

        val createMessage = restTemplate.createMessage(port, "sterus2", CreateMessageDto(
            "lol"
        ), createdTicket.id!!)
        assert201(createMessage)

        val messages = restTemplate.getMessage(port, createdTicket.id!!)
        assert200(messages)

        assertTrue { messages.body!!.size == 1 }
        assertTrue { messages.body!!.first().message == "lol" }

        restTemplate.deleteMessage(port, "sterus2", messages.body!!.first().id!!)
        val allMessages = restTemplate.getMessage(port, createdTicket.id!!)
        assertTrue { allMessages.body!!.isEmpty() }
    }
}