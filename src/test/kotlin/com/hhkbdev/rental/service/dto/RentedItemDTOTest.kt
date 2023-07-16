package com.hhkbdev.rental.service.dto

import com.hhkbdev.rental.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RentedItemDTOTest {

    @Test
    fun dtoEqualsVerifier() {
        equalsVerifier(RentedItemDTO::class)
        val rentedItemDTO1 = RentedItemDTO()
        rentedItemDTO1.id = 1L
        val rentedItemDTO2 = RentedItemDTO()
        assertThat(rentedItemDTO1).isNotEqualTo(rentedItemDTO2)
        rentedItemDTO2.id = rentedItemDTO1.id
        assertThat(rentedItemDTO1).isEqualTo(rentedItemDTO2)
        rentedItemDTO2.id = 2L
        assertThat(rentedItemDTO1).isNotEqualTo(rentedItemDTO2)
        rentedItemDTO1.id = null
        assertThat(rentedItemDTO1).isNotEqualTo(rentedItemDTO2)
    }
}
