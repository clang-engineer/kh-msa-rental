package com.hhkbdev.rental.web.rest

import com.hhkbdev.rental.repository.RentedItemRepository
import com.hhkbdev.rental.service.RentedItemService
import com.hhkbdev.rental.service.dto.RentedItemDTO
import com.hhkbdev.rental.web.rest.errors.BadRequestAlertException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import tech.jhipster.web.util.HeaderUtil
import tech.jhipster.web.util.PaginationUtil
import tech.jhipster.web.util.reactive.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import java.util.Objects

private const val ENTITY_NAME = "rentalRentedItem"
/**
 * REST controller for managing [com.hhkbdev.rental.domain.RentedItem].
 */
@RestController
@RequestMapping("/api")
class RentedItemResource(
    private val rentedItemService: RentedItemService,
    private val rentedItemRepository: RentedItemRepository,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val ENTITY_NAME = "rentalRentedItem"
    }

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /rented-items` : Create a new rentedItem.
     *
     * @param rentedItemDTO the rentedItemDTO to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new rentedItemDTO, or with status `400 (Bad Request)` if the rentedItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rented-items")
    fun createRentedItem(@RequestBody rentedItemDTO: RentedItemDTO): Mono<ResponseEntity<RentedItemDTO>> {
        log.debug("REST request to save RentedItem : $rentedItemDTO")
        if (rentedItemDTO.id != null) {
            throw BadRequestAlertException(
                "A new rentedItem cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        return rentedItemService.save(rentedItemDTO)
            .map { result ->
                try {
                    ResponseEntity.created(URI("/api/rented-items/${result.id}"))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
                        .body(result)
                } catch (e: URISyntaxException) {
                    throw RuntimeException(e)
                }
            }
    }

    /**
     * {@code PUT  /rented-items/:id} : Updates an existing rentedItem.
     *
     * @param id the id of the rentedItemDTO to save.
     * @param rentedItemDTO the rentedItemDTO to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated rentedItemDTO,
     * or with status `400 (Bad Request)` if the rentedItemDTO is not valid,
     * or with status `500 (Internal Server Error)` if the rentedItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rented-items/{id}")
    fun updateRentedItem(
        @PathVariable(value = "id", required = false) id: Long,
        @RequestBody rentedItemDTO: RentedItemDTO
    ): Mono<ResponseEntity<RentedItemDTO>> {
        log.debug("REST request to update RentedItem : {}, {}", id, rentedItemDTO)
        if (rentedItemDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }

        if (!Objects.equals(id, rentedItemDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        return rentedItemRepository.existsById(id).flatMap {
            if (!it) {
                return@flatMap Mono.error(BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"))
            }

            rentedItemService.update(rentedItemDTO)
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map { result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
                        .body(result)
                }
        }
    }

    /**
     * {@code PATCH  /rented-items/:id} : Partial updates given fields of an existing rentedItem, field will ignore if it is null
     *
     * @param id the id of the rentedItemDTO to save.
     * @param rentedItemDTO the rentedItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rentedItemDTO,
     * or with status {@code 400 (Bad Request)} if the rentedItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the rentedItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the rentedItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = ["/rented-items/{id}"], consumes = ["application/json", "application/merge-patch+json"])
    @Throws(URISyntaxException::class)
    fun partialUpdateRentedItem(
        @PathVariable(value = "id", required = false) id: Long,
        @RequestBody rentedItemDTO: RentedItemDTO
    ): Mono<ResponseEntity<RentedItemDTO>> {
        log.debug("REST request to partial update RentedItem partially : {}, {}", id, rentedItemDTO)
        if (rentedItemDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!Objects.equals(id, rentedItemDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        return rentedItemRepository.existsById(id).flatMap {
            if (!it) {
                return@flatMap Mono.error(BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"))
            }

            val result = rentedItemService.partialUpdate(rentedItemDTO)

            result
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map {
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, it.id.toString()))
                        .body(it)
                }
        }
    }

    /**
     * `GET  /rented-items` : get all the rentedItems.
     *
     * @param pageable the pagination information.
     * @param request a [ServerHttpRequest] request.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of rentedItems in body.
     */
    @GetMapping("/rented-items")
    fun getAllRentedItems(@org.springdoc.api.annotations.ParameterObject pageable: Pageable, request: ServerHttpRequest): Mono<ResponseEntity<List<RentedItemDTO>>> {

        log.debug("REST request to get a page of RentedItems")
        return rentedItemService.countAll()
            .zipWith(rentedItemService.findAll(pageable).collectList())
            .map {
                ResponseEntity.ok().headers(
                    PaginationUtil.generatePaginationHttpHeaders(
                        UriComponentsBuilder.fromHttpRequest(request),
                        PageImpl(it.t2, pageable, it.t1)
                    )
                ).body(it.t2)
            }
    }

    /**
     * `GET  /rented-items/:id` : get the "id" rentedItem.
     *
     * @param id the id of the rentedItemDTO to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the rentedItemDTO, or with status `404 (Not Found)`.
     */
    @GetMapping("/rented-items/{id}")
    fun getRentedItem(@PathVariable id: Long): Mono<ResponseEntity<RentedItemDTO>> {
        log.debug("REST request to get RentedItem : $id")
        val rentedItemDTO = rentedItemService.findOne(id)
        return ResponseUtil.wrapOrNotFound(rentedItemDTO)
    }
    /**
     *  `DELETE  /rented-items/:id` : delete the "id" rentedItem.
     *
     * @param id the id of the rentedItemDTO to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/rented-items/{id}")
    fun deleteRentedItem(@PathVariable id: Long): Mono<ResponseEntity<Void>> {
        log.debug("REST request to delete RentedItem : $id")
        return rentedItemService.delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build<Void>()
                )
            )
    }
}
