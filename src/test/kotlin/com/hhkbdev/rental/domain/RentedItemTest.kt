package com.hhkbdev.rental.domain

import com.hhkbdev.rental.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RentedItemTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(RentedItem::class)
        val rentedItem1 = RentedItem()
        rentedItem1.id = 1L
        val rentedItem2 = RentedItem()
        rentedItem2.id = rentedItem1.id
        assertThat(rentedItem1).isEqualTo(rentedItem2)
        rentedItem2.id = 2L
        assertThat(rentedItem1).isNotEqualTo(rentedItem2)
        rentedItem1.id = null
        assertThat(rentedItem1).isNotEqualTo(rentedItem2)
    }
}
