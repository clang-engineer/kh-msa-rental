package com.hhkbdev.rental.service.dto

import java.io.Serializable

data class BookInfoDTO(
    var id: Long? = null,
    var title: String? = null,
) : Serializable