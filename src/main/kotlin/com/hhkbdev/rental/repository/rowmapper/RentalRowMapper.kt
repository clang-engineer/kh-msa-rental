package com.hhkbdev.rental.repository.rowmapper

import com.hhkbdev.rental.domain.Rental
import com.hhkbdev.rental.domain.enumeration.RentalStatus
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
        entity.userId = converter.fromRow(row, prefix + "_user_id", Long::class.java)
        entity.rentalStatus = converter.fromRow(row, prefix + "_rental_status", RentalStatus::class.java)
        return entity
    }
}
