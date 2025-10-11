package ru.sterus.vs.highload.it

import kotlin.test.Test
import kotlin.test.assertEquals

class EchoTest : BaseTest() {
    @Test
    fun testEcho() {
        val response = restTemplate.getForEntity("/echo/asdweqwe", Map::class.java)
        assertEquals("asdweqwe",response.body?.get("message"))
    }
}