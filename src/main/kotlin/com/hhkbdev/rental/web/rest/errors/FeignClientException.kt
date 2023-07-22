package com.hhkbdev.rental.web.rest.errors

class FeignClientException(
    val status: Int, val errorMessage: String, val headers: Map<String, Collection<String>>
) : RuntimeException(errorMessage) {

    companion object {
        private const val serialVersionUID = 1L
    }
}