package ru.sterus.vs.highload.repositories

import org.jooq.impl.DSL
import org.jooq.impl.TableImpl
import org.jooq.Field
import java.time.LocalDateTime
import java.util.UUID

class Mapping {
    object TICKET : TableImpl<Nothing>(DSL.name("ticket")) {
        val ID: Field<UUID> = DSL.field("ticket.id", UUID::class.java)
        val TITLE: Field<String> = DSL.field("title", String::class.java)
        val DESCRIPTION: Field<String> = DSL.field("description", String::class.java)
        val CREATED_AT: Field<LocalDateTime> = DSL.field("ticket.created_at", LocalDateTime::class.java)
        val STATUS_ID: Field<Int> = DSL.field("status_id", Int::class.java)
        val AUTHOR_ID: Field<UUID> = DSL.field("author_id", UUID::class.java)
        val GROUP_ID: Field<UUID> = DSL.field("group_id", UUID::class.java)
    }

    object USER_GROUPS : TableImpl<Nothing>(DSL.name("user_groups")) {
        val ID: Field<UUID> = DSL.field("user_groups.id", UUID::class.java)
        val CREATED_AT: Field<LocalDateTime> = DSL.field("user_groups.created_at", LocalDateTime::class.java)
        val NAME: Field<String> = DSL.field("user_groups.name", String::class.java)
    }

    object USERS : TableImpl<Nothing>(DSL.name("users")) {
        val ID: Field<UUID> = DSL.field("users.id", UUID::class.java)
        val NAME: Field<String> = DSL.field("users.name", String::class.java)
    }

    object TICKET_STATUS : TableImpl<Nothing>(DSL.name("ticket_status")) {
        val ID: Field<Int> = DSL.field("ticket_status.id", Int::class.java)
        val STATUS: Field<String> = DSL.field("ticket_status.status", String::class.java)
    }

    object PARTICIPANT : TableImpl<Nothing>(DSL.name("participant")) {
        val USER_ID: Field<UUID> = DSL.field("user_id", UUID::class.java)
        val GROUP_ID: Field<UUID> = DSL.field("group_id", UUID::class.java)
        val ROLE_ID: Field<Int> = DSL.field("role_id", Int::class.java)
    }



}