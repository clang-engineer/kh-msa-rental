package com.hhkbdev.rental.repository

import org.springframework.data.relational.core.sql.Column
import org.springframework.data.relational.core.sql.Expression
import org.springframework.data.relational.core.sql.Table

class RentedItemSqlHelper {
    fun getColumns(table: Table, columnPrefix: String): MutableList<Expression> {
        val columns = mutableListOf<Expression>()
        columns.add(Column.aliased("id", table, columnPrefix + "_id"))
        columns.add(Column.aliased("book_id", table, columnPrefix + "_book_id"))
        columns.add(Column.aliased("rented_date", table, columnPrefix + "_rented_date"))
        columns.add(Column.aliased("due_date", table, columnPrefix + "_due_date"))

        columns.add(Column.aliased("rental_id", table, columnPrefix + "_rental_id"))
        return columns
    }
}
