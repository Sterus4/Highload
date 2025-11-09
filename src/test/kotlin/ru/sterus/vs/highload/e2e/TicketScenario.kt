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
import ru.sterus.vs.highload.e2e.util.createTicket
import ru.sterus.vs.highload.e2e.util.deleteTicket
import ru.sterus.vs.highload.e2e.util.getTicket
import ru.sterus.vs.highload.e2e.util.getUser
import ru.sterus.vs.highload.e2e.util.registerUser
import ru.sterus.vs.highload.e2e.util.updateTicket
import ru.sterus.vs.highload.enums.Role
import ru.sterus.vs.highload.model.dto.GroupAddUserDto
import ru.sterus.vs.highload.model.dto.GroupDto
import ru.sterus.vs.highload.model.dto.UserDto
import ru.sterus.vs.highload.model.dto.ticket.CreateTicketDto
import ru.sterus.vs.highload.model.dto.ticket.DeleteTicketDto
import ru.sterus.vs.highload.model.dto.ticket.GetTicketDto
import ru.sterus.vs.highload.model.dto.ticket.GroupFilter
import ru.sterus.vs.highload.model.dto.ticket.Page
import ru.sterus.vs.highload.model.dto.ticket.UpdateTicketDto
import ru.sterus.vs.highload.util.assert200
import ru.sterus.vs.highload.util.assert201
import kotlin.test.Test
import kotlin.test.assertTrue

@Sql("/sql/init.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class TicketScenario {
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
    fun `should create ticket`() {
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
        assert(createdTicket.title == "my-first-ticket")
        assert(createdTicket.description == "my-first-description")
    }

    @Test
    fun `should update ticket`() {
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

        val updateTicket = restTemplate.updateTicket(port, userId.body!!,
            UpdateTicketDto(
                createdTicket.id!!,
                "my-second-title",
                "my-second-description"
            )
        )
        val getUpdatedTicket = restTemplate.getTicket(port, GetTicketDto(
            page = Page(0, 50),
            groupFilter = GroupFilter(
                groups = listOf("my-second-group")
            )
        ))

        assert200(getUpdatedTicket)

        val updatedTicket = getUpdatedTicket.body!!.single()
        assert200(getTicket)

        assert(updatedTicket.title == "my-second-title")
        assert(updatedTicket.description =="my-second-description")
    }

    @Test
    fun `should delete ticket`() {
        val registerResponse = restTemplate.registerUser(port, UserDto("sterus3"))
        assert201(registerResponse)

        val userId = restTemplate.getUser(port, "sterus3")
        assert200(userId)

        val createGroup = restTemplate.createGroup(port, GroupDto("my-third-group"))
        assert201(createGroup)

        val addUserResponse = restTemplate.addUserGroup(port, GroupAddUserDto(GroupDto("my-third-group"), "sterus3", Role.ADMIN))
        assert200(addUserResponse)

        val createTicket = restTemplate.createTicket(port, userId.body!!, CreateTicketDto(
            "my-first-ticket",
            "my-first-description",
            "my-third-group",
        ))
        assert201(createTicket)

        val getTicket = restTemplate.getTicket(port, GetTicketDto(
            page = Page(0, 50),
            groupFilter = GroupFilter(
                groups = listOf("my-third-group")
            )
        ))

        assert200(getTicket)

        val createdTicket = getTicket.body!!.single()

        val deleteTicket = restTemplate.deleteTicket(port, userId.body!!, DeleteTicketDto(
            createdTicket.id!!
        ))
        assert200(deleteTicket)
        val getAll = restTemplate.getTicket(port, GetTicketDto(
            page = Page(0, 50),
            groupFilter = GroupFilter(
                groups = listOf("my-third-group")
            )
        ))
        assertTrue { getAll.body!!.isEmpty()}
    }
}