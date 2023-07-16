package com.hhkbdev.rental.service.mapper

import com.hhkbdev.rental.domain.Rental
import com.hhkbdev.rental.domain.RentedItem
import com.hhkbdev.rental.service.dto.RentalDTO
import com.hhkbdev.rental.service.dto.RentedItemDTO
import org.mapstruct.*

/**
 * Mapper for the entity [RentedItem] and its DTO [RentedItemDTO].
 */
@Mapper(componentModel = "spring")
interface RentedItemMapper :
    EntityMapper<RentedItemDTO, RentedItem> {

    @Mappings(
        Mapping(target = "rental", source = "rental", qualifiedByName = ["rentalId"])
    )
    override fun toDto(s: RentedItem): RentedItemDTO

    @Named("rentalId")
    @BeanMapping(ignoreByDefault = true)

    @Mappings(
        Mapping(target = "id", source = "id")
    )
    fun toDtoRentalId(rental: Rental): RentalDTO
}
