package com.hhkbdev.rental.web.rest

import com.hhkbdev.rental.IntegrationTest
import com.hhkbdev.rental.domain.RentedItem
import com.hhkbdev.rental.repository.EntityManager
import com.hhkbdev.rental.repository.RentedItemRepository
import com.hhkbdev.rental.service.mapper.RentedItemMapper
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
import java.time.LocalDate
import java.time.ZoneId
import java.util.Random
import java.util.concurrent.atomic.AtomicLong
import kotlin.test.assertNotNull

/**
 * Integration tests for the [RentedItemResource] REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class RentedItemResourceIT {
    @Autowired
    private lateinit var rentedItemRepository: RentedItemRepository

    @Autowired
    private lateinit var rentedItemMapper: RentedItemMapper

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var webTestClient: WebTestClient

    private lateinit var rentedItem: RentedItem

    @AfterEach
    fun cleanup() {
        deleteEntities(em)
    }

    @BeforeEach
    fun initTest() {
        deleteEntities(em)
        rentedItem = createEntity(em)
    }

    @Test
    @Throws(Exception::class)
    fun createRentedItem() {
        val databaseSizeBeforeCreate = rentedItemRepository.findAll().collectList().block().size
        // Create the RentedItem
        val rentedItemDTO = rentedItemMapper.toDto(rentedItem)
        webTestClient.post().uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(rentedItemDTO))
            .exchange()
            .expectStatus().isCreated

        // Validate the RentedItem in the database
        val rentedItemList = rentedItemRepository.findAll().collectList().block()
        assertThat(rentedItemList).hasSize(databaseSizeBeforeCreate + 1)
        val testRentedItem = rentedItemList[rentedItemList.size - 1]

        assertThat(testRentedItem.bookId).isEqualTo(DEFAULT_BOOK_ID)
        assertThat(testRentedItem.rentedDate).isEqualTo(DEFAULT_RENTED_DATE)
        assertThat(testRentedItem.dueDate).isEqualTo(DEFAULT_DUE_DATE)
    }

    @Test
    @Throws(Exception::class)
    fun createRentedItemWithExistingId() {
        // Create the RentedItem with an existing ID
        rentedItem.id = 1L
        val rentedItemDTO = rentedItemMapper.toDto(rentedItem)

        val databaseSizeBeforeCreate = rentedItemRepository.findAll().collectList().block().size
        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient.post().uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(rentedItemDTO))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the RentedItem in the database
        val rentedItemList = rentedItemRepository.findAll().collectList().block()
        assertThat(rentedItemList).hasSize(databaseSizeBeforeCreate)
    }

    @Test

    fun getAllRentedItems() {
        // Initialize the database
        rentedItemRepository.save(rentedItem).block()

        // Get all the rentedItemList
        webTestClient.get().uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id").value(hasItem(rentedItem.id?.toInt()))
            .jsonPath("$.[*].bookId").value(hasItem(DEFAULT_BOOK_ID?.toInt()))
            .jsonPath("$.[*].rentedDate").value(hasItem(DEFAULT_RENTED_DATE.toString()))
            .jsonPath("$.[*].dueDate").value(hasItem(DEFAULT_DUE_DATE.toString()))
    }

    @Test

    fun getRentedItem() {
        // Initialize the database
        rentedItemRepository.save(rentedItem).block()

        val id = rentedItem.id
        assertNotNull(id)

        // Get the rentedItem
        webTestClient.get().uri(ENTITY_API_URL_ID, rentedItem.id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id").value(`is`(rentedItem.id?.toInt()))
            .jsonPath("$.bookId").value(`is`(DEFAULT_BOOK_ID?.toInt()))
            .jsonPath("$.rentedDate").value(`is`(DEFAULT_RENTED_DATE.toString()))
            .jsonPath("$.dueDate").value(`is`(DEFAULT_DUE_DATE.toString()))
    }
    @Test

    fun getNonExistingRentedItem() {
        // Get the rentedItem
        webTestClient.get().uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
    }
    @Test
    fun putExistingRentedItem() {
        // Initialize the database
        rentedItemRepository.save(rentedItem).block()

        val databaseSizeBeforeUpdate = rentedItemRepository.findAll().collectList().block().size

        // Update the rentedItem
        val updatedRentedItem = rentedItemRepository.findById(rentedItem.id).block()
        updatedRentedItem.bookId = UPDATED_BOOK_ID
        updatedRentedItem.rentedDate = UPDATED_RENTED_DATE
        updatedRentedItem.dueDate = UPDATED_DUE_DATE
        val rentedItemDTO = rentedItemMapper.toDto(updatedRentedItem)

        webTestClient.put().uri(ENTITY_API_URL_ID, rentedItemDTO.id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(rentedItemDTO))
            .exchange()
            .expectStatus().isOk

        // Validate the RentedItem in the database
        val rentedItemList = rentedItemRepository.findAll().collectList().block()
        assertThat(rentedItemList).hasSize(databaseSizeBeforeUpdate)
        val testRentedItem = rentedItemList[rentedItemList.size - 1]
        assertThat(testRentedItem.bookId).isEqualTo(UPDATED_BOOK_ID)
        assertThat(testRentedItem.rentedDate).isEqualTo(UPDATED_RENTED_DATE)
        assertThat(testRentedItem.dueDate).isEqualTo(UPDATED_DUE_DATE)
    }

    @Test
    fun putNonExistingRentedItem() {
        val databaseSizeBeforeUpdate = rentedItemRepository.findAll().collectList().block().size
        rentedItem.id = count.incrementAndGet()

        // Create the RentedItem
        val rentedItemDTO = rentedItemMapper.toDto(rentedItem)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient.put().uri(ENTITY_API_URL_ID, rentedItemDTO.id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(rentedItemDTO))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the RentedItem in the database
        val rentedItemList = rentedItemRepository.findAll().collectList().block()
        assertThat(rentedItemList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun putWithIdMismatchRentedItem() {
        val databaseSizeBeforeUpdate = rentedItemRepository.findAll().collectList().block().size
        rentedItem.id = count.incrementAndGet()

        // Create the RentedItem
        val rentedItemDTO = rentedItemMapper.toDto(rentedItem)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient.put().uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(rentedItemDTO))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the RentedItem in the database
        val rentedItemList = rentedItemRepository.findAll().collectList().block()
        assertThat(rentedItemList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun putWithMissingIdPathParamRentedItem() {
        val databaseSizeBeforeUpdate = rentedItemRepository.findAll().collectList().block().size
        rentedItem.id = count.incrementAndGet()

        // Create the RentedItem
        val rentedItemDTO = rentedItemMapper.toDto(rentedItem)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient.put().uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertObjectToJsonBytes(rentedItemDTO))
            .exchange()
            .expectStatus().isEqualTo(405)

        // Validate the RentedItem in the database
        val rentedItemList = rentedItemRepository.findAll().collectList().block()
        assertThat(rentedItemList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun partialUpdateRentedItemWithPatch() {
        rentedItemRepository.save(rentedItem).block()

        val databaseSizeBeforeUpdate = rentedItemRepository.findAll().collectList().block().size

// Update the rentedItem using partial update
        val partialUpdatedRentedItem = RentedItem().apply {
            id = rentedItem.id

            bookId = UPDATED_BOOK_ID
            rentedDate = UPDATED_RENTED_DATE
            dueDate = UPDATED_DUE_DATE
        }

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRentedItem.id)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(convertObjectToJsonBytes(partialUpdatedRentedItem))
            .exchange()
            .expectStatus()
            .isOk

// Validate the RentedItem in the database
        val rentedItemList = rentedItemRepository.findAll().collectList().block()
        assertThat(rentedItemList).hasSize(databaseSizeBeforeUpdate)
        val testRentedItem = rentedItemList.last()
        assertThat(testRentedItem.bookId).isEqualTo(UPDATED_BOOK_ID)
        assertThat(testRentedItem.rentedDate).isEqualTo(UPDATED_RENTED_DATE)
        assertThat(testRentedItem.dueDate).isEqualTo(UPDATED_DUE_DATE)
    }

    @Test
    @Throws(Exception::class)
    fun fullUpdateRentedItemWithPatch() {
        rentedItemRepository.save(rentedItem).block()

        val databaseSizeBeforeUpdate = rentedItemRepository.findAll().collectList().block().size

// Update the rentedItem using partial update
        val partialUpdatedRentedItem = RentedItem().apply {
            id = rentedItem.id

            bookId = UPDATED_BOOK_ID
            rentedDate = UPDATED_RENTED_DATE
            dueDate = UPDATED_DUE_DATE
        }

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRentedItem.id)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(convertObjectToJsonBytes(partialUpdatedRentedItem))
            .exchange()
            .expectStatus()
            .isOk

// Validate the RentedItem in the database
        val rentedItemList = rentedItemRepository.findAll().collectList().block()
        assertThat(rentedItemList).hasSize(databaseSizeBeforeUpdate)
        val testRentedItem = rentedItemList.last()
        assertThat(testRentedItem.bookId).isEqualTo(UPDATED_BOOK_ID)
        assertThat(testRentedItem.rentedDate).isEqualTo(UPDATED_RENTED_DATE)
        assertThat(testRentedItem.dueDate).isEqualTo(UPDATED_DUE_DATE)
    }

    @Throws(Exception::class)
    fun patchNonExistingRentedItem() {
        val databaseSizeBeforeUpdate = rentedItemRepository.findAll().collectList().block().size
        rentedItem.id = count.incrementAndGet()

        // Create the RentedItem
        val rentedItemDTO = rentedItemMapper.toDto(rentedItem)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient.patch().uri(ENTITY_API_URL_ID, rentedItemDTO.id)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(convertObjectToJsonBytes(rentedItemDTO))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the RentedItem in the database
        val rentedItemList = rentedItemRepository.findAll().collectList().block()
        assertThat(rentedItemList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun patchWithIdMismatchRentedItem() {
        val databaseSizeBeforeUpdate = rentedItemRepository.findAll().collectList().block().size
        rentedItem.id = count.incrementAndGet()

        // Create the RentedItem
        val rentedItemDTO = rentedItemMapper.toDto(rentedItem)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient.patch().uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(convertObjectToJsonBytes(rentedItemDTO))
            .exchange()
            .expectStatus().isBadRequest

        // Validate the RentedItem in the database
        val rentedItemList = rentedItemRepository.findAll().collectList().block()
        assertThat(rentedItemList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun patchWithMissingIdPathParamRentedItem() {
        val databaseSizeBeforeUpdate = rentedItemRepository.findAll().collectList().block().size
        rentedItem.id = count.incrementAndGet()

        // Create the RentedItem
        val rentedItemDTO = rentedItemMapper.toDto(rentedItem)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient.patch().uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(convertObjectToJsonBytes(rentedItemDTO))
            .exchange()
            .expectStatus().isEqualTo(405)

        // Validate the RentedItem in the database
        val rentedItemList = rentedItemRepository.findAll().collectList().block()
        assertThat(rentedItemList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test

    fun deleteRentedItem() {
        // Initialize the database
        rentedItemRepository.save(rentedItem).block()
        val databaseSizeBeforeDelete = rentedItemRepository.findAll().collectList().block().size
        // Delete the rentedItem
        webTestClient.delete().uri(ENTITY_API_URL_ID, rentedItem.id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent

        // Validate the database contains one less item
        val rentedItemList = rentedItemRepository.findAll().collectList().block()
        assertThat(rentedItemList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_BOOK_ID: Long = 1L
        private const val UPDATED_BOOK_ID: Long = 2L

        private val DEFAULT_RENTED_DATE: LocalDate = LocalDate.ofEpochDay(0L)
        private val UPDATED_RENTED_DATE: LocalDate = LocalDate.now(ZoneId.systemDefault())

        private val DEFAULT_DUE_DATE: LocalDate = LocalDate.ofEpochDay(0L)
        private val UPDATED_DUE_DATE: LocalDate = LocalDate.now(ZoneId.systemDefault())

        private val ENTITY_API_URL: String = "/api/rented-items"
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
        fun createEntity(em: EntityManager): RentedItem {
            val rentedItem = RentedItem(
                bookId = DEFAULT_BOOK_ID,

                rentedDate = DEFAULT_RENTED_DATE,

                dueDate = DEFAULT_DUE_DATE

            )

            return rentedItem
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): RentedItem {
            val rentedItem = RentedItem(
                bookId = UPDATED_BOOK_ID,

                rentedDate = UPDATED_RENTED_DATE,

                dueDate = UPDATED_DUE_DATE

            )

            return rentedItem
        }

        fun deleteEntities(em: EntityManager) {
            try {
                em.deleteAll(RentedItem::class.java).block()
            } catch (e: Exception) {
                // It can fail, if other entities are still referring this - it will be removed later.
            }
        }
    }
}
