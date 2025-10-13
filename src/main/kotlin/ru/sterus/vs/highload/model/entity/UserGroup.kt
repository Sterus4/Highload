package ru.sterus.vs.highload.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.ColumnDefault
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "user_groups")
open class UserGroup {
    @Id
    @Column(name = "id", nullable = false)
    open var id: UUID? = null

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    open var createdAt: OffsetDateTime? = null
}