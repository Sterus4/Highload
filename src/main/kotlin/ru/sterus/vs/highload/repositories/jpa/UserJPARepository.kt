package ru.sterus.vs.highload.repositories.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.sterus.vs.highload.model.entity.User
import java.util.UUID

@Repository
interface UserJPARepository : JpaRepository<User, UUID> {
    fun findUserByName(name: String): User?
}
