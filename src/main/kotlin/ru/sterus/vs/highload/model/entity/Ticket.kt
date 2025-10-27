package ru.sterus.vs.highload.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.ColumnDefault
import ru.sterus.vs.highload.enums.TicketStatusEnum
import ru.sterus.vs.highload.helper.intFromStatus
import ru.sterus.vs.highload.model.dto.CreateTicketDto
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "ticket")
open class Ticket {
    @Id
    @Column(name = "id", nullable = false)
    open var id: UUID? = null

    @NotNull
    @Column(name = "title", nullable = false, length = Integer.MAX_VALUE)
    open var title: String? = null

    @Column(name = "description", length = Integer.MAX_VALUE)
    open var description: String? = null

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    open var createdAt: OffsetDateTime? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "status_id", nullable = false)
    open var status: TicketStatus? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    open var author: User? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    open var group: UserGroup? = null

    companion object {
        fun fromDto(createTicketDto: CreateTicketDto) = Ticket().apply {
            title = createTicketDto.title
            description = createTicketDto.description
        }
    }
}