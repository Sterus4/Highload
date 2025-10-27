package ru.sterus.vs.highload.service.impl

import org.springframework.stereotype.Service
import ru.sterus.vs.highload.service.UserService
import lombok.RequiredArgsConstructor
import ru.sterus.vs.highload.model.dto.UserDto
import ru.sterus.vs.highload.model.entity.User
import ru.sterus.vs.highload.repositories.UserRepository

@Service
@RequiredArgsConstructor
class UserServiceImpl(private val userRepository: UserRepository) : UserService {

    override fun registerUser(user: UserDto) {
        userRepository.saveUser(User.fromDto(user))
    }
}