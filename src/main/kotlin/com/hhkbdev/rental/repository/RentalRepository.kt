package com.hhkbdev.rental.repository

import com.hhkbdev.rental.domain.Rental
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
* Spring Data R2DBC repository for the Rental entity.
*/
@SuppressWarnings("unused")
@Repository
interface RentalRepository : ReactiveCrudRepository<Rental, Long>, RentalRepositoryInternal {

    override fun findAllBy(pageable: Pageable?): Flux<Rental>

    override fun <S : Rental> save(entity: S): Mono<S>

    override fun findAll(): Flux<Rental>

    override fun findById(id: Long?): Mono<Rental>

    override fun deleteById(id: Long): Mono<Void>

    fun findByUserId(userId: Long): Mono<Rental>
}

interface RentalRepositoryInternal {
    fun <S : Rental> save(entity: S): Mono<S>

    fun findAllBy(pageable: Pageable?): Flux<Rental>

    fun findAll(): Flux<Rental>

    fun findById(id: Long?): Mono<Rental>

    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // fun findAllBy(pageable: Pageable, criteria: Criteria): Flux<Rental>
}
