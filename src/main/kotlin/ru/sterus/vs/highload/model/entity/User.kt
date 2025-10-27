package ru.sterus.vs.highload.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import ru.sterus.vs.highload.model.dto.UserDto
import java.util.UUID

@Entity
@Table(name = "users")
open class User {
    @Id
    @Column(name = "id", nullable = false)
    open var id: UUID? = null

    @Size(max = 50)
    @NotNull
    @Column(name = "name", nullable = false, length = 50)
    open var name: String? = null

    companion object {
        fun fromDto(userDto: UserDto) = User().apply {
            name = userDto.name
        }
    }
}