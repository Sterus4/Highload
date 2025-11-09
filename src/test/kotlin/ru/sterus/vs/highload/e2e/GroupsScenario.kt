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
import ru.sterus.vs.highload.e2e.util.getUser
import ru.sterus.vs.highload.e2e.util.getUserGroups
import ru.sterus.vs.highload.e2e.util.registerUser
import ru.sterus.vs.highload.enums.Role
import ru.sterus.vs.highload.model.dto.GroupAddUserDto
import ru.sterus.vs.highload.model.dto.GroupDto
import ru.sterus.vs.highload.model.dto.UserDto
import ru.sterus.vs.highload.util.*
import kotlin.test.Test
import kotlin.test.assertTrue

@Sql("/sql/init.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class GroupsScenario {
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
    fun `should create user via http`() {
        val registerResponse = restTemplate.registerUser(port, UserDto("sterus1"))
        assert201(registerResponse)

        val registerResponse2 = restTemplate.registerUser(port, UserDto("sterus1"))
        assert400(registerResponse2)

        val userId = restTemplate.getUser(port, "sterus1")
        assert200(userId)

        val userGroups = restTemplate.getUserGroups(port, "sterus1")
        assert200(userGroups)
        assertTrue(userGroups.body!!.isEmpty())
    }

    @Test
    fun `should create group via http`() {
        val registerResponse = restTemplate.registerUser(port, UserDto("sterus2"))
        assert201(registerResponse)

        val userId = restTemplate.getUser(port, "sterus2")
        assert200(userId)

        val createGroup = restTemplate.createGroup(port, GroupDto("my-first-group"))
        assert201(createGroup)

        val addUserResponse = restTemplate.addUserGroup(port, GroupAddUserDto(GroupDto("my-first-group"), "sterus2", Role.ADMIN))
        assert200(addUserResponse)

        val userGroups = restTemplate.getUserGroups(port, "sterus2")
        assert200(userGroups)
        assertTrue(userGroups.body!!.map { it.groupName }.contains("my-first-group"))
    }
}