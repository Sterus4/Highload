package ru.sterus.vs.highload.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.validation.constraints.NotNull
import org.hibernate.Hibernate
import java.io.Serializable
import java.util.Objects
import java.util.UUID

@Embeddable
open class LinkId : Serializable {
    @NotNull
    @Column(name = "first_ticket_id", nullable = false)
    open var firstTicketId: UUID? = null

    @NotNull
    @Column(name = "second_ticket_id", nullable = false)
    open var secondTicketId: UUID? = null
    override fun hashCode(): Int = Objects.hash(firstTicketId, secondTicketId)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as LinkId

        return firstTicketId == other.firstTicketId &&
            secondTicketId == other.secondTicketId
    }

    companion object {
        private const val serialVersionUID = 0L
    }
}