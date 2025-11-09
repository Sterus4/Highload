package ru.sterus.vs.highload.model.dto

import java.time.OffsetDateTime
import java.util.UUID

class MessageDto {
    var id: Long? = null
    var ticketId: UUID? = null
    var message: String? = null
    var createdAt: OffsetDateTime? = null
    var createdBy: String? = null
}
