package ru.sterus.vs.highload.it.repository.jooq

import org.junit.jupiter.api.Assertions.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestConstructor
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import ru.sterus.vs.highload.exception.ProcessRequestException
import ru.sterus.vs.highload.model.entity.User
import ru.sterus.vs.highload.repositories.UserRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

//TODO доделать тесты, дальше добавить функциональность, поправить конфиги, поправить нейминг в json и path's

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class UserRepositoryTest(
    private  val userRepository: UserRepository
) {
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
    }

    @Test
    fun createOneUserTest(){
        userRepository.saveUser(User().apply {
            this.name = "testUser"
        })

        assertNotNull(userRepository.getUserByName("testUser").singleOrNull())
    }

    @Test
    fun sameUserNameTest(){
        val userName = "anotherUser"
        userRepository.saveUser(User().apply {
            this.name = userName
        })
        val e = assertThrows(ProcessRequestException::class.java) {
            userRepository.saveUser(User().apply {
                this.name = userName
            })
        }
        assertEquals("User <$userName> already exists", e.message)
    }
}