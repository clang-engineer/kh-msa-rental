package com.hhkbdev.rental.service.dto

import com.hhkbdev.rental.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RentalDTOTest {

    @Test
    fun dtoEqualsVerifier() {
        equalsVerifier(RentalDTO::class)
        val rentalDTO1 = RentalDTO()
        rentalDTO1.id = 1L
        val rentalDTO2 = RentalDTO()
        assertThat(rentalDTO1).isNotEqualTo(rentalDTO2)
        rentalDTO2.id = rentalDTO1.id
        assertThat(rentalDTO1).isEqualTo(rentalDTO2)
        rentalDTO2.id = 2L
        assertThat(rentalDTO1).isNotEqualTo(rentalDTO2)
        rentalDTO1.id = null
        assertThat(rentalDTO1).isNotEqualTo(rentalDTO2)
    }
}
