package com.hhkbdev.rental.service.dto

import java.io.Serializable
import java.util.Objects
import javax.validation.constraints.*

/**
 * A DTO for the [com.hhkbdev.rental.domain.Rental] entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
data class RentalDTO(

    var id: Long? = null,

    @get: NotNull(message = "must not be null")
    @get: Size(min = 5, max = 20)
    var title: String? = null,

    var description: String? = null
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
