package ru.sterus.vs.highload.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.ColumnDefault
import ru.sterus.vs.highload.model.dto.GroupDto
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "user_groups")
open class UserGroup {
    @Id
    @Column(name = "id", nullable = false)
    open var id: UUID? = null

    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    open var createdAt: OffsetDateTime? = null

    @Column(name = "name", length = Integer.MAX_VALUE)
    open var name: String? = null

    companion object {
        fun fromDto(groupDto: GroupDto) = UserGroup().apply {
            name = groupDto.name
        }
    }
}