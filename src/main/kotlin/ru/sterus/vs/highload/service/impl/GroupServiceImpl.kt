package ru.sterus.vs.highload.service.impl

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import ru.sterus.vs.highload.exception.ProcessRequestException
import ru.sterus.vs.highload.model.dto.GroupDto
import ru.sterus.vs.highload.enums.Role
import ru.sterus.vs.highload.model.entity.UserGroup
import ru.sterus.vs.highload.repositories.GroupRepository
import ru.sterus.vs.highload.repositories.UserRepository
import ru.sterus.vs.highload.service.GroupService
import java.util.UUID

// @RequiredArgsConstructor
@Service
class GroupServiceImpl(val groupRepository: GroupRepository, val userRepository: UserRepository) : GroupService {
    override fun createGroup(group: GroupDto, currentUser: UUID) {
        val userGroups = groupRepository.getUserGroups(currentUser)
        if (!userGroups.map { it.groupName }.contains("SUPER")){
            throw ProcessRequestException(HttpStatus.FORBIDDEN, "You are not allowed to create a group")
        }
        groupRepository.createGroup(UserGroup.fromDto(group))
    }

    private fun addUserToGroup(groupDto: GroupDto, userToAdd: String, role: Role) {
        val group = groupRepository.getGroupByName(groupDto.name)
        val usersByName = userRepository.getUserByName(userToAdd)
        if (group.size != 1){
            throw ProcessRequestException(HttpStatus.BAD_REQUEST, "Group <${groupDto.name}> does not exist")
        }
        if (usersByName.size != 1) {
            throw ProcessRequestException(HttpStatus.BAD_REQUEST, "User <$userToAdd> does not exist")
        }
        groupRepository.addUser(group.single()?.id!!, usersByName.single().id!!, role)
    }

    override fun addUser(group: GroupDto, currentUser: UUID, userToAdd: String, role: Role) {
        val currentUserGroups = groupRepository.getUserGroups(currentUser)
        val currentUserGroupsName = currentUserGroups.map { it.groupName }
        if (!currentUserGroupsName.contains("SUPER") && !currentUserGroupsName.contains(group.name)){
            throw ProcessRequestException(HttpStatus.FORBIDDEN, "You are not allowed to manage group <${group.name}>")
        }
        if (currentUserGroupsName.contains("SUPER")){
            addUserToGroup(group, userToAdd, role)
            return
        }
        val userRoleInGroup = currentUserGroups.filter { it.groupName == group.name }.map { it.role }.toSet()

        if(!userRoleInGroup.contains("ADMIN")){
            throw ProcessRequestException(HttpStatus.FORBIDDEN, "You are not allowed to manage group <${group.name}>")
        }
        addUserToGroup(group, userToAdd, role)
    }
}