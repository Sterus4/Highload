package ru.sterus.vs.highload.it

import org.junit.jupiter.api.Disabled
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class BaseTest {

    @Autowired
    protected lateinit var restTemplate: TestRestTemplate

    companion object {
        //TODO доделать тесты, дальше добавить функциональность, поправить конфиги, поправить нейминг в json и path's
        @Container
        @JvmStatic
        @ServiceConnection
        val postgres = PostgreSQLContainer<Nothing>("postgres:16").apply {
            waitingFor(Wait.forListeningPorts(5432))
        }
    }
}