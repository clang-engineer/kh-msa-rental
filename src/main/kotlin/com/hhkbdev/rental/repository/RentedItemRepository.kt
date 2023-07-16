package com.hhkbdev.rental.repository

import com.hhkbdev.rental.domain.RentedItem
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
* Spring Data R2DBC repository for the RentedItem entity.
*/
@SuppressWarnings("unused")
@Repository
interface RentedItemRepository : ReactiveCrudRepository<RentedItem, Long>, RentedItemRepositoryInternal {

    override fun findAllBy(pageable: Pageable?): Flux<RentedItem>

    @Query("SELECT * FROM rented_item entity WHERE entity.rental_id = :id")
    fun findByRental(id: Long): Flux<RentedItem>

    @Query("SELECT * FROM rented_item entity WHERE entity.rental_id IS NULL")
    fun findAllWhereRentalIsNull(): Flux<RentedItem>

    override fun <S : RentedItem> save(entity: S): Mono<S>

    override fun findAll(): Flux<RentedItem>

    override fun findById(id: Long?): Mono<RentedItem>

    override fun deleteById(id: Long): Mono<Void>
}

interface RentedItemRepositoryInternal {
    fun <S : RentedItem> save(entity: S): Mono<S>

    fun findAllBy(pageable: Pageable?): Flux<RentedItem>

    fun findAll(): Flux<RentedItem>

    fun findById(id: Long?): Mono<RentedItem>

    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // fun findAllBy(pageable: Pageable, criteria: Criteria): Flux<RentedItem>
}
