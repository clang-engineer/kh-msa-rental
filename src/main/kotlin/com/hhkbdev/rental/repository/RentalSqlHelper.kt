package com.hhkbdev.rental.repository

import org.springframework.data.relational.core.sql.Column
import org.springframework.data.relational.core.sql.Expression
import org.springframework.data.relational.core.sql.Table

class RentalSqlHelper {
    fun getColumns(table: Table, columnPrefix: String): MutableList<Expression> {
        val columns = mutableListOf<Expression>()
        columns.add(Column.aliased("id", table, columnPrefix + "_id"))
        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"))
        columns.add(Column.aliased("rental_status", table, columnPrefix + "_rental_status"))

        return columns
    }
}
