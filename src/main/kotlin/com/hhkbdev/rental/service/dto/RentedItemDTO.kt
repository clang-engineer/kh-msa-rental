package com.hhkbdev.rental.service.dto

import java.io.Serializable
import java.time.LocalDate
import java.util.Objects

/**
 * A DTO for the [com.hhkbdev.rental.domain.RentedItem] entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
data class RentedItemDTO(

    var id: Long? = null,

    var bookId: Long? = null,

    var rentedDate: LocalDate? = null,

    var dueDate: LocalDate? = null,

    var rental: RentalDTO? = null
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RentedItemDTO) return false
        val rentedItemDTO = other
        if (this.id == null) {
            return false
        }
        return Objects.equals(this.id, rentedItemDTO.id)
    }

    override fun hashCode() = Objects.hash(this.id)
}
