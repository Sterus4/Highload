package ru.sterus.vs.highload.repositories.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.sterus.vs.highload.model.entity.Ticket
import java.util.UUID

@Repository
interface TicketJPARepository : JpaRepository<Ticket, UUID> {
    fun findTicketById(id: UUID): Ticket?
}
