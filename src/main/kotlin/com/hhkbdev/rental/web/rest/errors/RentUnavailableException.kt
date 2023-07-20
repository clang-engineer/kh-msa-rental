package com.hhkbdev.rental.web.rest.errors

data class RentUnavailableException(
    override val message: String?
) : RuntimeException(message)
