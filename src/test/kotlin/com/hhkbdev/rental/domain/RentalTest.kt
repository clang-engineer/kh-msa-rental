package com.hhkbdev.rental.domain

import com.hhkbdev.rental.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RentalTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Rental::class)
        val rental1 = Rental()
        rental1.id = 1L
        val rental2 = Rental()
        rental2.id = rental1.id
        assertThat(rental1).isEqualTo(rental2)
        rental2.id = 2L
        assertThat(rental1).isNotEqualTo(rental2)
        rental1.id = null
        assertThat(rental1).isNotEqualTo(rental2)
    }
}
