package ru.sterus.vs.highload.repositories

import org.jooq.impl.DSL
import org.jooq.impl.TableImpl
import org.jooq.Field
import java.time.LocalDateTime
import java.util.UUID

class Mapping {
    object TICKET : TableImpl<Nothing>(DSL.name("ticket")) {
        val ID: Field<UUID> = DSL.field("id", UUID::class.java)
        val TITLE: Field<String> = DSL.field("title", String::class.java)
        val DESCRIPTION: Field<String> = DSL.field("description", String::class.java)
        val CREATED_AT: Field<LocalDateTime> = DSL.field("created_at", LocalDateTime::class.java)
        val STATUS_ID: Field<Int> = DSL.field("status_id", Int::class.java)
        val AUTHOR_ID: Field<UUID> = DSL.field("author_id", UUID::class.java)
        val GROUP_ID: Field<UUID> = DSL.field("group_id", UUID::class.java)
    }

}