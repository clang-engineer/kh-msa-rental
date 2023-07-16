package com.hhkbdev.rental.repository.rowmapper

import com.hhkbdev.rental.domain.RentedItem
import io.r2dbc.spi.Row
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.function.BiFunction

/**
 * Converter between {@link Row} to {@link RentedItem}, with proper type conversions.
 */
@Service
class RentedItemRowMapper(val converter: ColumnConverter) : BiFunction<Row, String, RentedItem> {

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link RentedItem} stored in the database.
     */
    override fun apply(row: Row, prefix: String): RentedItem {
        val entity = RentedItem()
        entity.id = converter.fromRow(row, prefix + "_id", Long::class.java)
        entity.bookId = converter.fromRow(row, prefix + "_book_id", Long::class.java)
        entity.rentedDate = converter.fromRow(row, prefix + "_rented_date", LocalDate::class.java)
        entity.dueDate = converter.fromRow(row, prefix + "_due_date", LocalDate::class.java)
        entity.rentalId = converter.fromRow(row, prefix + "_rental_id", Long::class.java)
        return entity
    }
}
