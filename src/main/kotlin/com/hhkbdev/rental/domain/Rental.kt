package com.hhkbdev.rental.domain

import com.hhkbdev.rental.domain.enumeration.RentalStatus
import com.hhkbdev.rental.web.rest.errors.RentUnavailableException
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import java.time.LocalDate
import javax.persistence.CascadeType
import javax.persistence.OneToMany

/**
 * A Rental.
 */
@Table("rental")
@SuppressWarnings("common-java:DuplicatedBlocks")
data class Rental(
    @Id
    @Column("id")
    var id: Long? = null,

    @Column("user_id")
    var userId: Long? = null,

    @Column("rental_status")
    var rentalStatus: RentalStatus? = null,

    @Column("late_fee")
    var lateFee: Long? = null,

    @OneToMany(mappedBy = "rental", cascade = [CascadeType.ALL], orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    var rentedItems: MutableSet<RentedItem> = mutableSetOf(),

    @OneToMany(mappedBy = "rental", cascade = [CascadeType.ALL], orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    var returnedItems: MutableSet<ReturnedItem> = mutableSetOf()
) : Serializable {

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Rental) return false
        return id != null && other.id != null && id == other.id
    }

    override fun toString(): String {
        return "Rental{" +
            "id=" + id +
            ", userId=" + userId +
            ", rentalStatus='" + rentalStatus + "'" +
            "}"
    }

    fun checkRentalAvailable(): Boolean {
        if (rentalStatus === RentalStatus.RENT_UNAVAILABLE || lateFee != 0L) {
            throw RentUnavailableException("Rent is unavailable")
        }

        if (rentedItems.size >= 5) {
            throw RentUnavailableException("Rent is unavailable")
        }
        return true
    }

    fun rentBook(bookId: Long, title: String): Rental {
        checkRentalAvailable()
        rentedItems.add(RentedItem.createRentedItem(bookId, title, LocalDate.now()))

        return this
    }

    fun returnBook(bookId: Long): Rental {
        val rentedItem = rentedItems.find { it.bookId == bookId }
        returnedItems.add(
            ReturnedItem.createReturnedItem(
                rentedItem?.bookId,
                rentedItem?.bookTitle,
                LocalDate.now()
            )
        )
        rentedItems.remove(rentedItem)
        return this
    }

    companion object {
        private const val serialVersionUID = 1L

        fun createRental(userId: Long): Rental {
            return Rental().apply {
                this.userId = userId
                this.rentalStatus = RentalStatus.RENT_AVAILABLE
                this.lateFee = 0
            }
        }
    }
}
