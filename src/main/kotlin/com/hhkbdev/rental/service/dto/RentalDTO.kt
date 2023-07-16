package com.hhkbdev.rental.service.dto

import com.hhkbdev.rental.domain.enumeration.RentalStatus
import java.io.Serializable
import java.util.Objects

/**
 * A DTO for the [com.hhkbdev.rental.domain.Rental] entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
data class RentalDTO(

    var id: Long? = null,

    var userId: Long? = null,

    var rentalStatus: RentalStatus? = null
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RentalDTO) return false
        val rentalDTO = other
        if (this.id == null) {
            return false
        }
        return Objects.equals(this.id, rentalDTO.id)
    }

    override fun hashCode() = Objects.hash(this.id)
}
