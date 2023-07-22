package com.hhkbdev.rental.service.impl

import com.hhkbdev.rental.domain.Rental
import com.hhkbdev.rental.repository.RentalRepository
import com.hhkbdev.rental.service.RentalService
import com.hhkbdev.rental.service.dto.RentalDTO
import com.hhkbdev.rental.service.mapper.RentalMapper
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service Implementation for managing [Rental].
 */
@Service
@Transactional
class RentalServiceImpl(
    private val rentalRepository: RentalRepository,
    private val rentalMapper: RentalMapper,
) : RentalService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun save(rentalDTO: RentalDTO): Mono<RentalDTO> {
        log.debug("Request to save Rental : $rentalDTO")
        return rentalRepository.save(rentalMapper.toEntity(rentalDTO))
            .map(rentalMapper::toDto)
    }

    override fun update(rentalDTO: RentalDTO): Mono<RentalDTO> {
        log.debug("Request to update Rental : {}", rentalDTO)
        return rentalRepository.save(rentalMapper.toEntity(rentalDTO))
            .map(rentalMapper::toDto)
    }

    override fun partialUpdate(rentalDTO: RentalDTO): Mono<RentalDTO> {
        log.debug("Request to partially update Rental : {}", rentalDTO)

        return rentalRepository.findById(rentalDTO.id)
            .map {
                rentalMapper.partialUpdate(it, rentalDTO)
                it
            }
            .flatMap { rentalRepository.save(it) }
            .map { rentalMapper.toDto(it) }
    }

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Flux<RentalDTO> {
        log.debug("Request to get all Rentals")
        return rentalRepository.findAllBy(pageable)
            .map(rentalMapper::toDto)
    }

    override fun countAll() = rentalRepository.count()

    @Transactional(readOnly = true)
    override fun findOne(id: Long): Mono<RentalDTO> {
        log.debug("Request to get Rental : $id")
        return rentalRepository.findById(id)
            .map(rentalMapper::toDto)
    }

    override fun delete(id: Long): Mono<Void> {
        log.debug("Request to delete Rental : $id")
        return rentalRepository.deleteById(id)
    }

    override fun rentBook(userId: Long, bookId: Long, bookTitle: String): Mono<RentalDTO> {
        return rentalRepository.findByUserId(userId)
            .flatMap {
                it.checkRentalAvailable()
                it.rentBook(bookId, bookTitle)
                rentalRepository.save(it)
            }
            .flatMap {
                // todo: send event
                Mono.just(it)
            }
            .map {
                rentalMapper.toDto(it)
            }
    }

    override fun returnBook(userId: Long, bookId: Long): Mono<RentalDTO> {
        return rentalRepository.findByUserId(userId)
            .flatMap {
                it.checkRentalAvailable()
                it.returnBook(bookId)
                rentalRepository.save(it)
            }
            .flatMap {
                // todo: send event
                Mono.just(it)
            }
            .map {
                rentalMapper.toDto(it)
            }
    }
}
