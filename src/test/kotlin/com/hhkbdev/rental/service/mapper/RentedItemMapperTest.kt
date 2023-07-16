package com.hhkbdev.rental.service.mapper

import org.junit.jupiter.api.BeforeEach

class RentedItemMapperTest {

    private lateinit var rentedItemMapper: RentedItemMapper

    @BeforeEach
    fun setUp() {
        rentedItemMapper = RentedItemMapperImpl()
    }
}
