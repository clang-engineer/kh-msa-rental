package com.hhkbdev.rental.repository

import org.springframework.data.relational.core.sql.Column
import org.springframework.data.relational.core.sql.Expression
import org.springframework.data.relational.core.sql.Table

class RentalSqlHelper {
    fun getColumns(table: Table, columnPrefix: String): MutableList<Expression> {
        val columns = mutableListOf<Expression>()
        columns.add(Column.aliased("id", table, columnPrefix + "_id"))
        columns.add(Column.aliased("title", table, columnPrefix + "_title"))
        columns.add(Column.aliased("description", table, columnPrefix + "_description"))

        return columns
    }
}
