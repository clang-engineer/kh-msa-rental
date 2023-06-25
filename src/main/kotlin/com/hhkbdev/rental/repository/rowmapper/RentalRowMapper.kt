package com.hhkbdev.rental.repository.rowmapper

import com.hhkbdev.rental.domain.Rental
import io.r2dbc.spi.Row
import org.springframework.stereotype.Service
import java.util.function.BiFunction

/**
 * Converter between {@link Row} to {@link Rental}, with proper type conversions.
 */
@Service
class RentalRowMapper(val converter: ColumnConverter) : BiFunction<Row, String, Rental> {

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Rental} stored in the database.
     */
    override fun apply(row: Row, prefix: String): Rental {
        val entity = Rental()
        entity.id = converter.fromRow(row, prefix + "_id", Long::class.java)
        entity.title = converter.fromRow(row, prefix + "_title", String::class.java)
        entity.description = converter.fromRow(row, prefix + "_description", String::class.java)
        return entity
    }
}
