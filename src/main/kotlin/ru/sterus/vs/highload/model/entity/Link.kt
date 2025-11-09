package ru.sterus.vs.highload.model.entity

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table

@Entity
@Table(name = "link")
open class Link {
    @EmbeddedId
    open var id: LinkId? = null

    @MapsId("firstTicketId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "first_ticket_id", nullable = false)
    open var firstTicket: Ticket? = null

    @MapsId("secondTicketId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "second_ticket_id", nullable = false)
    open var secondTicket: Ticket? = null
}