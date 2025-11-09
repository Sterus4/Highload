package ru.sterus.vs.highload.repositories

import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.table
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import ru.sterus.vs.highload.repositories.Mapping.*
import ru.sterus.vs.highload.exception.ProcessRequestException
import ru.sterus.vs.highload.helper.intFromRole
import ru.sterus.vs.highload.enums.Role
import ru.sterus.vs.highload.model.dto.UserRoleGroup
import ru.sterus.vs.highload.model.entity.UserGroup
import ru.sterus.vs.highload.util.DBUtil
import java.util.UUID

@Repository
class GroupRepository(private val dsl: DSLContext, private val util: DBUtil) {
    fun getUserGroups(userId: UUID): List<UserRoleGroup> {
        return dsl.select(
            field("users.name").`as`("userName"),
            field("user_groups.name").`as`("groupName"),
            field("user_group_role.role").`as`("role")
        ).from(table("participant"))
            .join(table("users"))
            .on(field("users.id").eq(field("participant.user_id")))
            .join(table("user_groups"))
            .on(field("user_groups.id").eq(field("participant.group_id")))
            .join(table("user_group_role"))
            .on(field("user_group_role.id").eq(field("participant.role_id")))
            .where(field("participant.user_id").eq(userId))
            .fetchInto(UserRoleGroup::class.java)
    }

    fun createGroup(group: UserGroup){
        util.validate(group)
        val record = dsl.insertInto(table("user_groups"))
            .set(field("id"), UUID.randomUUID())
            .set(field("name"), group.name)
            .onConflict(field("name")).doNothing()
            .execute()
        if (record != 1){
            throw ProcessRequestException(HttpStatus.BAD_REQUEST, "Group <${group.name}> already exists")
        }
    }

    fun getGroupByName(groupName: String): List<UserGroup?> {
        return dsl.selectFrom(table("user_groups"))
            .where(field("name").eq(groupName))
            .fetchInto(UserGroup::class.java)
    }

    fun addUser(groupId: UUID, userToAddId: UUID, role: Role){
        val roleId = role.intFromRole() ?: throw ProcessRequestException(HttpStatus.BAD_REQUEST, "Role <$role> does not exists")
        val inserted = dsl.insertInto(table("participant"))
            .set(field("user_id"), userToAddId)
            .set(field("role_id"), roleId)
            .set(field("group_id"), groupId)
            .onConflictDoNothing()
            .execute()
        if (inserted != 1){
            throw ProcessRequestException(HttpStatus.BAD_REQUEST, "User already have role $role in this group")
        }
    }

    fun moveUser(userId: UUID, groupId: UUID, role: Role) {
        dsl.transaction { configuration ->
            val ctx = DSL.using(configuration)

            ctx.deleteFrom(PARTICIPANT)
                .where(PARTICIPANT.USER_ID.eq(userId))
                .execute()

            if (ctx.insertInto(PARTICIPANT)
                .columns(PARTICIPANT.USER_ID, PARTICIPANT.GROUP_ID, PARTICIPANT.ROLE_ID)
                .values(userId, groupId, role.intFromRole())
                .execute() != 1){
                throw ProcessRequestException(HttpStatus.BAD_REQUEST, "Failed to move user to group")
            }

            ctx.update(TICKET)
                .set(TICKET.GROUP_ID, groupId)
                .where(TICKET.AUTHOR_ID.eq(userId))
                .execute()
        }
    }
}