package ru.sterus.vs.highload.ut.util

import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import ru.sterus.vs.highload.exception.ProcessRequestException
import ru.sterus.vs.highload.util.ServiceUtil
import kotlin.test.assertEquals

class ServiceUtilTest {
    private val serviceUtil = ServiceUtil()

    @ParameterizedTest
    @ValueSource(strings = ["12345678-1234-5678-1234-567890123456"])
    fun testValidateCorrectUUID(uuid: String){
        serviceUtil.validateUUID(uuid)
    }

    @ParameterizedTest
    @ValueSource(strings = ["12345678123456781234567890123456", "uuid"])
    fun testValidateIncorrectUUID(uuid: String){
        val e = assertThrows<ProcessRequestException> { serviceUtil.validateUUID(uuid) }
        assertEquals("Not valid UUID: $uuid", e.message)
    }


}