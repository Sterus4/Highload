package ru.sterus.vs.highload.service.impl

import org.springframework.stereotype.Service
import ru.sterus.vs.highload.model.dto.GroupDto
import ru.sterus.vs.highload.service.UserService
import ru.sterus.vs.highload.model.dto.UserDto
import ru.sterus.vs.highload.model.dto.UserRoleGroup
import ru.sterus.vs.highload.model.entity.User
import ru.sterus.vs.highload.repositories.GroupRepository
import ru.sterus.vs.highload.repositories.UserRepository
import java.util.UUID

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository
) : UserService {

    override fun registerUser(user: UserDto) {
        userRepository.saveUser(User.fromDto(user))
    }

    override fun getUser(name: String): UUID {
        val user = userRepository.getUserByName(name).singleOrNull() ?: throw IllegalArgumentException("User not found")
        return user.id!!
    }

    override fun getUserGroups(name: String): List<UserRoleGroup> {
        val user = userRepository.getUserByName(name).singleOrNull() ?: throw IllegalArgumentException("User not found")
        return groupRepository.getUserGroups(user.id!!)
    }
}