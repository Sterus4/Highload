package ru.sterus.vs.highload.helper

import ru.sterus.vs.highload.enums.Role
import ru.sterus.vs.highload.enums.TicketStatusEnum
import ru.sterus.vs.highload.model.entity.TicketStatus

private val roleMap = mapOf(
    Role.ADMIN to 1,
    Role.PARTICIPANT to 2
)
fun Role.intFromRole(): Int? {
    return roleMap[this]
}

private val ticketStatusMap = mapOf(
    TicketStatusEnum.OPEN to 1,
    TicketStatusEnum.PROGRESS to 2,
    TicketStatusEnum.CLOSE to 3,
    TicketStatusEnum.CREATED to 4
)

fun TicketStatusEnum.intFromStatus(): Int? {
    return ticketStatusMap[this]
}