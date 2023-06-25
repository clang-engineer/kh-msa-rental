package com.hhkbdev.rental.web.rest

import com.hhkbdev.rental.IntegrationTest
import com.hhkbdev.rental.domain.Rental
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

        assertThat(testRental.title).isEqualTo(DEFAULT_TITLE)
        assertThat(testRental.description).isEqualTo(DEFAULT_DESCRIPTION)
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
    @Throws(Exception::class)
    fun checkTitleIsRequired() {
        val databaseSizeBeforeTest = rentalRepository.findAll().collectList().block().size
        // set the field null
        rental.title = null

        // Create the Rental, which fails.
        val rentalDTO = rentalMapper.toDto(rental)

        webTestClient.post().uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(rentalDTO))
            .exchange()
            .expectStatus().isBadRequest

        val rentalList = rentalRepository.findAll().collectList().block()
        assertThat(rentalList).hasSize(databaseSizeBeforeTest)
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
            .jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION))
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
            .jsonPath("$.title").value(`is`(DEFAULT_TITLE))
            .jsonPath("$.description").value(`is`(DEFAULT_DESCRIPTION))
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
        updatedRental.title = UPDATED_TITLE
        updatedRental.description = UPDATED_DESCRIPTION
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
        assertThat(testRental.title).isEqualTo(UPDATED_TITLE)
        assertThat(testRental.description).isEqualTo(UPDATED_DESCRIPTION)
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

            title = UPDATED_TITLE
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
        assertThat(testRental.title).isEqualTo(UPDATED_TITLE)
        assertThat(testRental.description).isEqualTo(DEFAULT_DESCRIPTION)
    }

    @Test
    @Throws(Exception::class)
    fun fullUpdateRentalWithPatch() {
        rentalRepository.save(rental).block()

        val databaseSizeBeforeUpdate = rentalRepository.findAll().collectList().block().size

// Update the rental using partial update
        val partialUpdatedRental = Rental().apply {
            id = rental.id

            title = UPDATED_TITLE
            description = UPDATED_DESCRIPTION
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
        assertThat(testRental.title).isEqualTo(UPDATED_TITLE)
        assertThat(testRental.description).isEqualTo(UPDATED_DESCRIPTION)
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

        private const val DEFAULT_TITLE = "AAAAAAAAAA"
        private const val UPDATED_TITLE = "BBBBBBBBBB"

        private const val DEFAULT_DESCRIPTION = "AAAAAAAAAA"
        private const val UPDATED_DESCRIPTION = "BBBBBBBBBB"

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
                title = DEFAULT_TITLE,

                description = DEFAULT_DESCRIPTION

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
                title = UPDATED_TITLE,

                description = UPDATED_DESCRIPTION

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
