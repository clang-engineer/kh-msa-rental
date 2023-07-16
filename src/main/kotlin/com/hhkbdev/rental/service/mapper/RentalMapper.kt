package com.hhkbdev.rental.service.mapper

import com.hhkbdev.rental.domain.Rental
import com.hhkbdev.rental.service.dto.RentalDTO
import org.mapstruct.*

/**
 * Mapper for the entity [Rental] and its DTO [RentalDTO].
 */
@Mapper(componentModel = "spring")
interface RentalMapper :
    EntityMapper<RentalDTO, Rental>
