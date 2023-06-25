package com.hhkbdev.rental.web.rest

import com.hhkbdev.rental.repository.RentalRepository
import com.hhkbdev.rental.service.RentalService
import com.hhkbdev.rental.service.dto.RentalDTO
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
import javax.validation.Valid
import javax.validation.constraints.NotNull

private const val ENTITY_NAME = "rentalRental"
/**
 * REST controller for managing [com.hhkbdev.rental.domain.Rental].
 */
@RestController
@RequestMapping("/api")
class RentalResource(
    private val rentalService: RentalService,
    private val rentalRepository: RentalRepository,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val ENTITY_NAME = "rentalRental"
    }

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /rentals` : Create a new rental.
     *
     * @param rentalDTO the rentalDTO to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new rentalDTO, or with status `400 (Bad Request)` if the rental has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rentals")
    fun createRental(@Valid @RequestBody rentalDTO: RentalDTO): Mono<ResponseEntity<RentalDTO>> {
        log.debug("REST request to save Rental : $rentalDTO")
        if (rentalDTO.id != null) {
            throw BadRequestAlertException(
                "A new rental cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        return rentalService.save(rentalDTO)
            .map { result ->
                try {
                    ResponseEntity.created(URI("/api/rentals/${result.id}"))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
                        .body(result)
                } catch (e: URISyntaxException) {
                    throw RuntimeException(e)
                }
            }
    }

    /**
     * {@code PUT  /rentals/:id} : Updates an existing rental.
     *
     * @param id the id of the rentalDTO to save.
     * @param rentalDTO the rentalDTO to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated rentalDTO,
     * or with status `400 (Bad Request)` if the rentalDTO is not valid,
     * or with status `500 (Internal Server Error)` if the rentalDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rentals/{id}")
    fun updateRental(
        @PathVariable(value = "id", required = false) id: Long,
        @Valid @RequestBody rentalDTO: RentalDTO
    ): Mono<ResponseEntity<RentalDTO>> {
        log.debug("REST request to update Rental : {}, {}", id, rentalDTO)
        if (rentalDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }

        if (!Objects.equals(id, rentalDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        return rentalRepository.existsById(id).flatMap {
            if (!it) {
                return@flatMap Mono.error(BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"))
            }

            rentalService.update(rentalDTO)
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map { result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
                        .body(result)
                }
        }
    }

    /**
     * {@code PATCH  /rentals/:id} : Partial updates given fields of an existing rental, field will ignore if it is null
     *
     * @param id the id of the rentalDTO to save.
     * @param rentalDTO the rentalDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rentalDTO,
     * or with status {@code 400 (Bad Request)} if the rentalDTO is not valid,
     * or with status {@code 404 (Not Found)} if the rentalDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the rentalDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = ["/rentals/{id}"], consumes = ["application/json", "application/merge-patch+json"])
    @Throws(URISyntaxException::class)
    fun partialUpdateRental(
        @PathVariable(value = "id", required = false) id: Long,
        @NotNull @RequestBody rentalDTO: RentalDTO
    ): Mono<ResponseEntity<RentalDTO>> {
        log.debug("REST request to partial update Rental partially : {}, {}", id, rentalDTO)
        if (rentalDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!Objects.equals(id, rentalDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        return rentalRepository.existsById(id).flatMap {
            if (!it) {
                return@flatMap Mono.error(BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"))
            }

            val result = rentalService.partialUpdate(rentalDTO)

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
     * `GET  /rentals` : get all the rentals.
     *
     * @param pageable the pagination information.
     * @param request a [ServerHttpRequest] request.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of rentals in body.
     */
    @GetMapping("/rentals")
    fun getAllRentals(@org.springdoc.api.annotations.ParameterObject pageable: Pageable, request: ServerHttpRequest): Mono<ResponseEntity<List<RentalDTO>>> {

        log.debug("REST request to get a page of Rentals")
        return rentalService.countAll()
            .zipWith(rentalService.findAll(pageable).collectList())
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
     * `GET  /rentals/:id` : get the "id" rental.
     *
     * @param id the id of the rentalDTO to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the rentalDTO, or with status `404 (Not Found)`.
     */
    @GetMapping("/rentals/{id}")
    fun getRental(@PathVariable id: Long): Mono<ResponseEntity<RentalDTO>> {
        log.debug("REST request to get Rental : $id")
        val rentalDTO = rentalService.findOne(id)
        return ResponseUtil.wrapOrNotFound(rentalDTO)
    }
    /**
     *  `DELETE  /rentals/:id` : delete the "id" rental.
     *
     * @param id the id of the rentalDTO to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/rentals/{id}")
    fun deleteRental(@PathVariable id: Long): Mono<ResponseEntity<Void>> {
        log.debug("REST request to delete Rental : $id")
        return rentalService.delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build<Void>()
                )
            )
    }
}
