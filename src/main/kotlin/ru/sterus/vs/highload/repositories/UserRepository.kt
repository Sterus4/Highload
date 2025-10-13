package ru.sterus.vs.highload.repositories

import org.jooq.DSLContext
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.table
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import ru.sterus.vs.highload.exception.ProcessRequestException
import ru.sterus.vs.highload.model.entity.User
import java.util.UUID

@Repository
class UserRepository(private val dsl: DSLContext, private val util: DBUtil) {
    fun saveUser(user: User) {
        util.validate(user)
        val record = dsl.insertInto(table("users"))
            .set(field("id"), UUID.randomUUID())
            .set(field("name"), user.name)
            .onConflict(field("name")).doNothing()
            .execute()
        if (record != 1){
           throw ProcessRequestException(HttpStatus.BAD_REQUEST, "User <${user.name}> already exists")
        }
    }
}