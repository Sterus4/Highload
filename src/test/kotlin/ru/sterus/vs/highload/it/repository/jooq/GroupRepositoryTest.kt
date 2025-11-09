package ru.sterus.vs.highload.it.repository.jooq

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeAll
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
import ru.sterus.vs.highload.exception.ProcessRequestException
import ru.sterus.vs.highload.model.dto.UserRoleGroup
import ru.sterus.vs.highload.model.entity.User
import ru.sterus.vs.highload.model.entity.UserGroup
import ru.sterus.vs.highload.repositories.GroupRepository
import ru.sterus.vs.highload.repositories.UserRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class GroupRepositoryTest(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
) {
    private final val USER_NAME = "sterus"

    companion object {
        @BeforeAll
        @JvmStatic
        fun createUser(@Autowired context: ApplicationContext){
            val userRepository = context.getBean(UserRepository::class.java)
            userRepository.saveUser(User().apply { this.name = "sterus" })
            userRepository.saveUser(User().apply { this.name = "sterus2" })
        }
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
    }

    @Test
    fun baseCreateGroupTest() {
        val groupName = "Some"
        groupRepository.createGroup(UserGroup().apply { this.name = groupName })

        assertNotNull(groupRepository.getGroupByName(groupName).singleOrNull())
    }

    @Test
    fun sameGroupNameTest(){
        val groupName = "Same"
        groupRepository.createGroup(UserGroup().apply { this.name = groupName })
        assertThrows (ProcessRequestException::class.java){
            groupRepository.createGroup(UserGroup().apply { this.name = groupName })
        }
    }

    @Test
    fun addUserToGroupTest(){
        val groupName = "Some another"
        groupRepository.createGroup(UserGroup().apply { this.name = groupName })
        val group = groupRepository.getGroupByName(groupName).single()!!
        val user = userRepository.getUserByName(USER_NAME).single()
        groupRepository.addUser(group.id!!, user.id!!, Role.ADMIN)

        assertEquals(
            UserRoleGroup(groupName, "Admin", user.name!!),
            groupRepository.getUserGroups(user.id!!).single()
        )
    }

    @Test
    fun addUserToGroupDoubleTest(){
        val groupName = "Some another2"
        groupRepository.createGroup(UserGroup().apply { this.name = groupName })
        val group = groupRepository.getGroupByName(groupName).single()!!
        val user = userRepository.getUserByName(USER_NAME).single()
        groupRepository.addUser(group.id!!, user.id!!, Role.ADMIN)

        val e = assertThrows(ProcessRequestException::class.java){
            groupRepository.addUser(group.id!!, user.id!!, Role.ADMIN)
        }
        assertEquals("User already have role ADMIN in this group",e.message)
    }

    @Test
    fun getUserGroupsTest(){
        val groupName1 = "Some anotherGroup1"
        val groupName2 = "Some anotherGroup2"
        groupRepository.createGroup(UserGroup().apply { this.name = groupName1 })
        groupRepository.createGroup(UserGroup().apply { this.name = groupName2 })
        val group1 = groupRepository.getGroupByName(groupName1).single()!!
        val group2 = groupRepository.getGroupByName(groupName2).single()!!

        val user = userRepository.getUserByName("sterus2").single()
        groupRepository.addUser(group1.id!!, user.id!!, Role.ADMIN)
        groupRepository.addUser(group2.id!!, user.id!!, Role.ADMIN)
        val userGroups = groupRepository.getUserGroups(user.id!!)

        assertEquals(2, userGroups.size)
    }
}