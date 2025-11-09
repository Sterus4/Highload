package ru.sterus.vs.highload.service

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import ru.sterus.vs.highload.exception.ProcessRequestException
import ru.sterus.vs.highload.repositories.GroupRepository
import java.util.UUID

@Service
class UserAccessService(private val groupRepository: GroupRepository) {
    fun isUserAdminInGroup(userId: UUID, groupName: String): Boolean {
        val currentUserGroups = groupRepository.getUserGroups(userId).filter { it.groupName == groupName }
        return currentUserGroups.any { it.role == "ADMIN" }
    }

    fun isUserParticipateInGroup(userId: UUID, groupName: String): Boolean {
        val currentUserGroups = groupRepository.getUserGroups(userId).map { it.groupName }
        return currentUserGroups.contains(groupName)
    }

    fun isUserSuper(userId: UUID): Boolean {
        val currentUserGroups = groupRepository.getUserGroups(userId).map { it.groupName }
        return currentUserGroups.contains("SUPER")
    }

    fun assertUserAdminInGroup(userId: UUID, groupName: String) {
        if (!isUserAdminInGroup(userId, groupName)) {
            throw ProcessRequestException(HttpStatus.FORBIDDEN, "You are not ADMIN in group <$groupName>")
        }
    }

    fun assertUserParticipateInGroup(userId: UUID, groupName: String) {
        if (!isUserParticipateInGroup(userId, groupName)) {
            throw ProcessRequestException(HttpStatus.FORBIDDEN, "You are not in group <$groupName>")
        }
    }
}