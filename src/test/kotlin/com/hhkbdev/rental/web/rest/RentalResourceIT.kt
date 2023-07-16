package com.hhkbdev.rental.web.rest

import com.hhkbdev.rental.IntegrationTest
import com.hhkbdev.rental.domain.Rental
import com.hhkbdev.rental.domain.enumeration.RentalStatus
import com.hhkbdev.rental.repository.EntityManager
import com.hhkbdev.rental.repository.RentalRepository
import com.hhkbdev.rental.service.mapper.RentalMapper
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.Random
import java.util.concurrent.atomic.AtomicLong
import kotlin.test.assertNotNull

/**
 * Integration tests for the [RentalResource] REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class RentalResourceIT {
    @Autowired
    private lateinit var rentalRepository: RentalRepository

    @Autowired
    private lateinit var rentalMapper: RentalMapper

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var webTestClient: WebTestClient

    private lateinit var rental: Rental

    @AfterEach
    fun cleanup() {
        deleteEntities(em)
    }

    @BeforeEach
    fun initTest() {
        deleteEntities(em)
        rental = createEntity(em)
    }

    @Test
    @Throws(Exception::class)
    fun createRental() {
        val databaseSizeBeforeCreate = rentalRepository.findAll().collectList().block().size
        // Create the Rental
        val rentalDTO = rentalMapper.toDto(rental)
        webTestClient.post().uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(rentalDTO))
            .exchange()
            .expectStatus().isCreated

        // Validate the Rental in the database
        val rentalList = rentalRepository.findAll().collectList().block()
        assertThat(rentalList).hasSize(databaseSizeBeforeCreate + 1)
        val testRental = rentalList[rentalList.size - 1]

        assertThat(testRental.userId).isEqualTo(DEFAULT_USER_ID)
        assertThat(testRental.rentalStatus).isEqualTo(DEFAULT_RENTAL_STATUS)
    }

    @Test
    @Throws(Exception::class)
    fun createRentalWithExistingId() {
        // Create the Rental with an existing ID
        rental.id = 1L
        val rentalDTO = rentalMapper.toDto(rental)

        val databaseSizeBeforeCreate = rentalRepository.findAll().collectList().block().size
        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient.post().uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(rentalDTO))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the Rental in the database
        val rentalList = rentalRepository.findAll().collectList().block()
        assertThat(rentalList).hasSize(databaseSizeBeforeCreate)
    }

    @Test

    fun getAllRentals() {
        // Initialize the database
        rentalRepository.save(rental).block()

        // Get all the rentalList
        webTestClient.get().uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id").value(hasItem(rental.id?.toInt()))
            .jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID?.toInt()))
            .jsonPath("$.[*].rentalStatus").value(hasItem(DEFAULT_RENTAL_STATUS.toString()))
    }

    @Test

    fun getRental() {
        // Initialize the database
        rentalRepository.save(rental).block()

        val id = rental.id
        assertNotNull(id)

        // Get the rental
        webTestClient.get().uri(ENTITY_API_URL_ID, rental.id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id").value(`is`(rental.id?.toInt()))
            .jsonPath("$.userId").value(`is`(DEFAULT_USER_ID?.toInt()))
            .jsonPath("$.rentalStatus").value(`is`(DEFAULT_RENTAL_STATUS.toString()))
    }
    @Test

    fun getNonExistingRental() {
        // Get the rental
        webTestClient.get().uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
    }
    @Test
    fun putExistingRental() {
        // Initialize the database
        rentalRepository.save(rental).block()

        val databaseSizeBeforeUpdate = rentalRepository.findAll().collectList().block().size

        // Update the rental
        val updatedRental = rentalRepository.findById(rental.id).block()
        updatedRental.userId = UPDATED_USER_ID
        updatedRental.rentalStatus = UPDATED_RENTAL_STATUS
        val rentalDTO = rentalMapper.toDto(updatedRental)

        webTestClient.put().uri(ENTITY_API_URL_ID, rentalDTO.id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(rentalDTO))
            .exchange()
            .expectStatus().isOk

        // Validate the Rental in the database
        val rentalList = rentalRepository.findAll().collectList().block()
        assertThat(rentalList).hasSize(databaseSizeBeforeUpdate)
        val testRental = rentalList[rentalList.size - 1]
        assertThat(testRental.userId).isEqualTo(UPDATED_USER_ID)
        assertThat(testRental.rentalStatus).isEqualTo(UPDATED_RENTAL_STATUS)
    }

    @Test
    fun putNonExistingRental() {
        val databaseSizeBeforeUpdate = rentalRepository.findAll().collectList().block().size
        rental.id = count.incrementAndGet()

        // Create the Rental
        val rentalDTO = rentalMapper.toDto(rental)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient.put().uri(ENTITY_API_URL_ID, rentalDTO.id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(rentalDTO))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the Rental in the database
        val rentalList = rentalRepository.findAll().collectList().block()
        assertThat(rentalList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun putWithIdMismatchRental() {
        val databaseSizeBeforeUpdate = rentalRepository.findAll().collectList().block().size
        rental.id = count.incrementAndGet()

        // Create the Rental
        val rentalDTO = rentalMapper.toDto(rental)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient.put().uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(rentalDTO))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the Rental in the database
        val rentalList = rentalRepository.findAll().collectList().block()
        assertThat(rentalList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun putWithMissingIdPathParamRental() {
        val databaseSizeBeforeUpdate = rentalRepository.findAll().collectList().block().size
        rental.id = count.incrementAndGet()

        // Create the Rental
        val rentalDTO = rentalMapper.toDto(rental)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient.put().uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(rentalDTO))
            .exchange()
            .expectStatus().isEqualTo(405)

        // Validate the Rental in the database
        val rentalList = rentalRepository.findAll().collectList().block()
        assertThat(rentalList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun partialUpdateRentalWithPatch() {
        rentalRepository.save(rental).block()

        val databaseSizeBeforeUpdate = rentalRepository.findAll().collectList().block().size

// Update the rental using partial update
        val partialUpdatedRental = Rental().apply {
            id = rental.id

            userId = UPDATED_USER_ID
        }

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRental.id)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(convertObjectToJsonBytes(partialUpdatedRental))
            .exchange()
            .expectStatus()
            .isOk

// Validate the Rental in the database
        val rentalList = rentalRepository.findAll().collectList().block()
        assertThat(rentalList).hasSize(databaseSizeBeforeUpdate)
        val testRental = rentalList.last()
        assertThat(testRental.userId).isEqualTo(UPDATED_USER_ID)
        assertThat(testRental.rentalStatus).isEqualTo(DEFAULT_RENTAL_STATUS)
    }

    @Test
    @Throws(Exception::class)
    fun fullUpdateRentalWithPatch() {
        rentalRepository.save(rental).block()

        val databaseSizeBeforeUpdate = rentalRepository.findAll().collectList().block().size

// Update the rental using partial update
        val partialUpdatedRental = Rental().apply {
            id = rental.id

            userId = UPDATED_USER_ID
            rentalStatus = UPDATED_RENTAL_STATUS
        }

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRental.id)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(convertObjectToJsonBytes(partialUpdatedRental))
            .exchange()
            .expectStatus()
            .isOk

// Validate the Rental in the database
        val rentalList = rentalRepository.findAll().collectList().block()
        assertThat(rentalList).hasSize(databaseSizeBeforeUpdate)
        val testRental = rentalList.last()
        assertThat(testRental.userId).isEqualTo(UPDATED_USER_ID)
        assertThat(testRental.rentalStatus).isEqualTo(UPDATED_RENTAL_STATUS)
    }

    @Throws(Exception::class)
    fun patchNonExistingRental() {
        val databaseSizeBeforeUpdate = rentalRepository.findAll().collectList().block().size
        rental.id = count.incrementAndGet()

        // Create the Rental
        val rentalDTO = rentalMapper.toDto(rental)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient.patch().uri(ENTITY_API_URL_ID, rentalDTO.id)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(convertObjectToJsonBytes(rentalDTO))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the Rental in the database
        val rentalList = rentalRepository.findAll().collectList().block()
        assertThat(rentalList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun patchWithIdMismatchRental() {
        val databaseSizeBeforeUpdate = rentalRepository.findAll().collectList().block().size
        rental.id = count.incrementAndGet()

        // Create the Rental
        val rentalDTO = rentalMapper.toDto(rental)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient.patch().uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(convertObjectToJsonBytes(rentalDTO))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the Rental in the database
        val rentalList = rentalRepository.findAll().collectList().block()
        assertThat(rentalList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun patchWithMissingIdPathParamRental() {
        val databaseSizeBeforeUpdate = rentalRepository.findAll().collectList().block().size
        rental.id = count.incrementAndGet()

        // Create the Rental
        val rentalDTO = rentalMapper.toDto(rental)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient.patch().uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(convertObjectToJsonBytes(rentalDTO))
            .exchange()
            .expectStatus().isEqualTo(405)

        // Validate the Rental in the database
        val rentalList = rentalRepository.findAll().collectList().block()
        assertThat(rentalList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test

    fun deleteRental() {
        // Initialize the database
        rentalRepository.save(rental).block()
        val databaseSizeBeforeDelete = rentalRepository.findAll().collectList().block().size
        // Delete the rental
        webTestClient.delete().uri(ENTITY_API_URL_ID, rental.id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent

        // Validate the database contains one less item
        val rentalList = rentalRepository.findAll().collectList().block()
        assertThat(rentalList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_USER_ID: Long = 1L
        private const val UPDATED_USER_ID: Long = 2L

        private val DEFAULT_RENTAL_STATUS: RentalStatus = RentalStatus.RENT_AVAILABLE
        private val UPDATED_RENTAL_STATUS: RentalStatus = RentalStatus.RENT_UNAVAILABLE

        private val ENTITY_API_URL: String = "/api/rentals"
        private val ENTITY_API_URL_ID: String = ENTITY_API_URL + "/{id}"

        private val random: Random = Random()
        private val count: AtomicLong = AtomicLong(random.nextInt().toLong() + (2 * Integer.MAX_VALUE))

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Rental {
            val rental = Rental(
                userId = DEFAULT_USER_ID,

                rentalStatus = DEFAULT_RENTAL_STATUS

            )

            return rental
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Rental {
            val rental = Rental(
                userId = UPDATED_USER_ID,

                rentalStatus = UPDATED_RENTAL_STATUS

            )

            return rental
        }

        fun deleteEntities(em: EntityManager) {
            try {
                em.deleteAll(Rental::class.java).block()
            } catch (e: Exception) {
                // It can fail, if other entities are still referring this - it will be removed later.
            }
        }
    }
}
