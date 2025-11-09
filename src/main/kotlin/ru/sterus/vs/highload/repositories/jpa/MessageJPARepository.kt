package ru.sterus.vs.highload.repositories.jpa

import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.sterus.vs.highload.model.entity.Message
import java.util.UUID

@Repository
interface MessageJPARepository : JpaRepository<Message, Long> {
    @EntityGraph(attributePaths = ["createdBy"])
    fun findAllByTicketId(ticketId: UUID) : List<Message>

    fun findMessageById(id: Long) : Message?

    @Transactional
    fun deleteMessageById(id: Long)
}