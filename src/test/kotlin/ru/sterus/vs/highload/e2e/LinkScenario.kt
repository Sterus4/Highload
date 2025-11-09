package ru.sterus.vs.highload.e2e

import org.junit.jupiter.api.Order
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
import ru.sterus.vs.highload.e2e.util.createLink
import ru.sterus.vs.highload.e2e.util.createTicket
import ru.sterus.vs.highload.e2e.util.getLinks
import ru.sterus.vs.highload.e2e.util.getTicket
import ru.sterus.vs.highload.e2e.util.getUser
import ru.sterus.vs.highload.e2e.util.registerUser
import ru.sterus.vs.highload.enums.Role
import ru.sterus.vs.highload.model.dto.GroupAddUserDto
import ru.sterus.vs.highload.model.dto.GroupDto
import ru.sterus.vs.highload.model.dto.LinkDto
import ru.sterus.vs.highload.model.dto.UserDto
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
class LinkScenario {
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

    @Order(1)
    @Test
    fun `should link two tickets`(){
        val registerResponse = restTemplate.registerUser(port, UserDto("sterus1"))
        assert201(registerResponse)

        val userId = restTemplate.getUser(port, "sterus1")
        assert200(userId)

        val createGroup = restTemplate.createGroup(port, GroupDto("my-first-group"))
        assert201(createGroup)

        val addUserResponse = restTemplate.addUserGroup(port, GroupAddUserDto(GroupDto("my-first-group"), "sterus1", Role.ADMIN))
        assert200(addUserResponse)

        restTemplate.createTicket(port, userId.body!!, CreateTicketDto(
            "my-first-ticket",
            "my-first-description",
            "my-first-group",
        ))

        restTemplate.createTicket(port, userId.body!!, CreateTicketDto(
            "my-second-ticket",
            "my-second-description",
            "my-first-group",
        ))

        val getTicket = restTemplate.getTicket(port, GetTicketDto(
            page = Page(0, 2),
            groupFilter = GroupFilter(
                groups = listOf("my-first-group")
            )
        ))
        assert200(getTicket)
        assertTrue { getTicket.body!!.size == 2 }

        restTemplate.createLink(port, "sterus1",
            LinkDto(
                firstTicketId = getTicket.body!!.first().id!!,
                secondTicketId = getTicket.body!!.last().id!!
            )
        )

        val getLinks = restTemplate.getLinks(port, getTicket.body!!.first().id!!)
        assert200(getLinks)

        assertTrue { getLinks.body!!.first().secondTicketId == getTicket.body!!.last().id!! }
    }

    @Test
    @Order(2)
    fun `should unlink two tickets`(){

    }
}