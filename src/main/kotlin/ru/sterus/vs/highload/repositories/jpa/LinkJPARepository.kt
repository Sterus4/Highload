package ru.sterus.vs.highload.repositories.jpa

import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import ru.sterus.vs.highload.model.entity.Link
import ru.sterus.vs.highload.model.entity.LinkId
import java.util.UUID

interface LinkJPARepository : JpaRepository<Link, LinkId> {
    @Transactional
    fun deleteLinkById_FirstTicketIdOrId_SecondTicketId(firstTicketId: UUID, secondTicketId: UUID)
    fun findAllById_FirstTicketIdOrId_SecondTicketId(firstTicketId: UUID, secondTicketId: UUID): List<Link>
}