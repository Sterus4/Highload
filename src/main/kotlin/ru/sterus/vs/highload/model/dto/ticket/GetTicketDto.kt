package ru.sterus.vs.highload.model.dto.ticket

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import ru.sterus.vs.highload.enums.TicketStatusEnum
import java.util.UUID

data class Page(
    @field:NotNull
    @field:Min(0)
    var start: Int,
    @field:NotNull
    @field:Min(1)
    @field:Max(50)
    var size: Int
)

data class GroupFilter(
    @field:NotNull
    @field:Size(min = 1)
    var groups: List<String>
)

data class StatusFilter(
    @field:NotNull
    var status: TicketStatusEnum
)

data class GetTicketDto(
    @field:NotNull
    var page: Page,
    var groupFilter: GroupFilter? = null,
    var statusFilter: StatusFilter? = null
)