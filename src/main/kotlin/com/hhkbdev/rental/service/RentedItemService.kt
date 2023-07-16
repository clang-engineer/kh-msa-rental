package com.hhkbdev.rental.service
import com.hhkbdev.rental.service.dto.RentedItemDTO
import org.springframework.data.domain.Pageable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service Interface for managing [com.hhkbdev.rental.domain.RentedItem].
 */
interface RentedItemService {

    /**
     * Save a rentedItem.
     *
     * @param rentedItemDTO the entity to save.
     * @return the persisted entity.
     */
    fun save(rentedItemDTO: RentedItemDTO): Mono<RentedItemDTO>

    /**
     * Updates a rentedItem.
     *
     * @param rentedItemDTO the entity to update.
     * @return the persisted entity.
     */
    fun update(rentedItemDTO: RentedItemDTO): Mono<RentedItemDTO>

    /**
     * Partially updates a rentedItem.
     *
     * @param rentedItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    fun partialUpdate(rentedItemDTO: RentedItemDTO): Mono<RentedItemDTO>

    /**
     * Get all the rentedItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    fun findAll(pageable: Pageable): Flux<RentedItemDTO>

    /**
     * Returns the number of rentedItems available.
     * @return the number of entities in the database.
     */
    fun countAll(): Mono<Long>
    /**
     * Get the "id" rentedItem.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Mono<RentedItemDTO>

    /**
     * Delete the "id" rentedItem.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    fun delete(id: Long): Mono<Void>
}
