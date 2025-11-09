package ru.sterus.vs.highload.it.repository.jooq

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestConstructor
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import ru.sterus.vs.highload.enums.Role
import ru.sterus.vs.highload.enums.TicketStatusEnum
import ru.sterus.vs.highload.model.dto.ticket.DeleteTicketDto
import ru.sterus.vs.highload.model.dto.ticket.GetTicketDto
import ru.sterus.vs.highload.model.dto.ticket.GroupFilter
import ru.sterus.vs.highload.model.dto.ticket.Page
import ru.sterus.vs.highload.model.dto.ticket.StatusFilter
import ru.sterus.vs.highload.model.dto.ticket.Ticket
import ru.sterus.vs.highload.model.dto.ticket.UpdateTicketDto
import ru.sterus.vs.highload.model.entity.User
import ru.sterus.vs.highload.model.entity.UserGroup
import ru.sterus.vs.highload.repositories.GroupRepository
import ru.sterus.vs.highload.repositories.TicketRepository
import ru.sterus.vs.highload.repositories.UserRepository
import java.util.UUID
import kotlin.jvm.java
import kotlin.test.Test
import kotlin.test.assertEquals


@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class TicketRepositoryTest(
    private val ticketRepository: TicketRepository,
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository
) {

    private val myUUID1 = UUID.randomUUID()
    private val myUUID2 = UUID.randomUUID()

    private val someTicket = Ticket().apply {
        this.id = myUUID1
        this.title = "Some title"
        this.description = "aha"
    }

    private val someTicket2 = Ticket().apply {
        this.id = myUUID2
        this.title = "Some title"
        this.description = "aha"
    }


    companion object {
        @Container
        private val postgres = PostgreSQLContainer<Nothing>("postgres:16").apply {
            withDatabaseName("highload")
            withUsername("user")
            withPassword("password")
        }

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }

        @BeforeAll
        @JvmStatic
        fun createUser(@Autowired context: ApplicationContext){
            val userRepository = context.getBean(UserRepository::class.java)
            val groupRepository = context.getBean(GroupRepository::class.java)
            userRepository.saveUser(User().apply { this.name = "sterus" })
            groupRepository.createGroup(UserGroup().apply { this.name = "Some" })
            groupRepository.createGroup(UserGroup().apply { this.name = "Some2" })

            val group = groupRepository.getGroupByName("Some").single()!!
            val group2 = groupRepository.getGroupByName("Some2").single()!!
            val user = userRepository.getUserByName("sterus").single()
            groupRepository.addUser(group.id!!, user.id!!, Role.ADMIN)
            groupRepository.addUser(group2.id!!, user.id!!, Role.ADMIN)
        }
    }

    @Test
    fun baseCreateAndGetTicket() {
        val userId = userRepository.getUserByName("sterus").single().id!!
        val groupId = groupRepository.getGroupByName("Some").single()!!.id!!

        ticketRepository.create(
            someTicket,
            userId,
            groupId
        )
        val some = ticketRepository.get(
            GetTicketDto(
                page = Page(
                    start = 0,
                    size = 1
                ),
                groupFilter = GroupFilter(
                    groups = listOf("Some")
                ),
                statusFilter = StatusFilter(
                    status = TicketStatusEnum.CREATED
                )
            )
        )
        val created = some.single()
        assertNotNull(created)
        assertEquals(someTicket.title, created.title)
        assertEquals(someTicket.description, created.description)
    }

    @Test
    fun getOneTest(){
        val userId = userRepository.getUserByName("sterus").single().id!!
        val groupId = groupRepository.getGroupByName("Some").single()!!.id!!

        ticketRepository.create(
            someTicket,
            userId,
            groupId
        )

        val some = ticketRepository.get(
            GetTicketDto(
                page = Page(
                    start = 0,
                    size = 1
                )
            )
        )
        val created = some.single()
        val getOneTicket = ticketRepository.getOneTicket(created.id!!)!!

        assertEquals(created.title, getOneTicket.title)
        assertEquals(created.description, getOneTicket.description)
        assertEquals(created.ticket_created_at, getOneTicket.ticket_created_at)
    }

    @Test
    fun updateTicket(){
        val userId = userRepository.getUserByName("sterus").single().id!!
        val groupId = groupRepository.getGroupByName("Some").single()!!.id!!
        ticketRepository.create(
            someTicket2,
            userId,
            groupId
        )

        val created = ticketRepository.get(
            GetTicketDto(
                page = Page(
                    start = 0,
                    size = 1
                )
            )
        ).single()

        ticketRepository.updateTicket(
            UpdateTicketDto(
                id = created.id!!,
                title = "New title",
                description = "New description",
            )
        )

        val updated = ticketRepository.get(
            GetTicketDto(
                page = Page(
                    start = 0,
                    size = 1
                )
            )
        ).single()

        assertEquals("New title", updated.title)
        assertEquals("New description", updated.description)
    }

    @Test
    fun deleteTicketTest() {
        val userId = userRepository.getUserByName("sterus").single().id!!
        val groupId = groupRepository.getGroupByName("Some2").single()!!.id!!
        ticketRepository.create(
            someTicket2,
            userId,
            groupId
        )

        val created = ticketRepository.get(
            GetTicketDto(
                page = Page(
                    start = 0,
                    size = 1
                ),
                groupFilter = GroupFilter(
                    groups = listOf("Some2")
                ),
            )
        ).single()

        ticketRepository.deleteTicket(DeleteTicketDto(id = created.id!!))

        val deleted = ticketRepository.get(
            GetTicketDto(
                page = Page(
                    start = 0,
                    size = 1
                ),
                groupFilter = GroupFilter(
                    groups = listOf("Some2")
                ),
            )
        )
        assertEquals(deleted.size, 0)
    }
}