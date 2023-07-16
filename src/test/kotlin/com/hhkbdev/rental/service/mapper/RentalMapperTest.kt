package com.hhkbdev.rental.service.mapper

import org.junit.jupiter.api.BeforeEach

class RentalMapperTest {

    private lateinit var rentalMapper: RentalMapper

    @BeforeEach
    fun setUp() {
        rentalMapper = RentalMapperImpl()
    }
}
