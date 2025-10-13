package ru.sterus.vs.highload.service

import ru.sterus.vs.highload.model.entity.UserDto

interface UserService {
    fun registerUser(user: UserDto)
}