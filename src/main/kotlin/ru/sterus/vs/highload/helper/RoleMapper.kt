package ru.sterus.vs.highload.helper

import ru.sterus.vs.highload.model.dto.Role

private val map = mapOf(
    Role.ADMIN to 1,
    Role.PARTICIPANT to 2
)
fun Role.intFromRole(): Int? {
    return map[this]
}