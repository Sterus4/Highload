package ru.sterus.vs.highload.service

import ru.sterus.vs.highload.model.dto.UserDto

interface UserService {
    fun registerUser(user: UserDto)
}