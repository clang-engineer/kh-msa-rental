package com.hhkbdev.rental.service
import com.hhkbdev.rental.domain.Rental
import com.hhkbdev.rental.service.dto.RentalDTO
import org.springframework.data.domain.Pageable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface RentalService {

    fun save(rentalDTO: RentalDTO): Mono<RentalDTO>

    fun update(rentalDTO: RentalDTO): Mono<RentalDTO>

    fun partialUpdate(rentalDTO: RentalDTO): Mono<RentalDTO>

    fun findAll(pageable: Pageable): Flux<RentalDTO>

    fun countAll(): Mono<Long>

    fun findOne(id: Long): Mono<RentalDTO>

    fun delete(id: Long): Mono<Void>

    fun rentBook(userId: Long, bookId: Long, bookTitle: String): Mono<RentalDTO>

    fun returnBook(userId: Long, bookId: Long, bookTitle: String): Mono<RentalDTO>
}
