package ru.sterus.vs.highload.repositories

import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class TicketRepository(private val dsl: DSLContext) {
}