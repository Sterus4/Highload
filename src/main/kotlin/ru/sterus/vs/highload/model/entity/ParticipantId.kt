package ru.sterus.vs.highload.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.validation.constraints.NotNull
import org.hibernate.Hibernate
import java.io.Serializable
import java.util.Objects
import java.util.UUID

@Embeddable
open class ParticipantId : Serializable {
    @NotNull
    @Column(name = "user_id", nullable = false)
    open var userId: UUID? = null

    @NotNull
    @Column(name = "group_id", nullable = false)
    open var groupId: UUID? = null

    @NotNull
    @Column(name = "role_id", nullable = false)
    open var roleId: Int? = null
    override fun hashCode(): Int = Objects.hash(userId, groupId, roleId)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as ParticipantId

        return userId == other.userId &&
            groupId == other.groupId &&
            roleId == other.roleId
    }

    companion object {
        private const val serialVersionUID = 0L
    }
}