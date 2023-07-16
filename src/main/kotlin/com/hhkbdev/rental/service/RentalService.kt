package com.hhkbdev.rental.service
import com.hhkbdev.rental.service.dto.RentalDTO
import org.springframework.data.domain.Pageable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service Interface for managing [com.hhkbdev.rental.domain.Rental].
 */
interface RentalService {

    /**
     * Save a rental.
     *
     * @param rentalDTO the entity to save.
     * @return the persisted entity.
     */
    fun save(rentalDTO: RentalDTO): Mono<RentalDTO>

    /**
     * Updates a rental.
     *
     * @param rentalDTO the entity to update.
     * @return the persisted entity.
     */
    fun update(rentalDTO: RentalDTO): Mono<RentalDTO>

    /**
     * Partially updates a rental.
     *
     * @param rentalDTO the entity to update partially.
     * @return the persisted entity.
     */
    fun partialUpdate(rentalDTO: RentalDTO): Mono<RentalDTO>

    /**
     * Get all the rentals.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    fun findAll(pageable: Pageable): Flux<RentalDTO>

    /**
     * Returns the number of rentals available.
     * @return the number of entities in the database.
     */
    fun countAll(): Mono<Long>
    /**
     * Get the "id" rental.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Mono<RentalDTO>

    /**
     * Delete the "id" rental.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    fun delete(id: Long): Mono<Void>
}
