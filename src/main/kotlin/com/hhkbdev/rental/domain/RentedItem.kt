package com.hhkbdev.rental.domain

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import java.time.LocalDate

/**
 * A RentedItem.
 */
@Table("rented_item")
@SuppressWarnings("common-java:DuplicatedBlocks")
data class RentedItem(

    @Id
    @Column("id")
    var id: Long? = null,
    @Column("book_id")
    var bookId: Long? = null,
    @Column("rented_date")
    var rentedDate: LocalDate? = null,
    @Column("due_date")
    var dueDate: LocalDate? = null,

    @Column("rental_id")
    var rentalId: Long? = null,
    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    @Transient
    var rental: Rental? = null

    fun rental(rental: Rental?): RentedItem {
        this.rental = rental
        return this
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RentedItem) return false
        return id != null && other.id != null && id == other.id
    }

    override fun toString(): String {
        return "RentedItem{" +
            "id=" + id +
            ", bookId=" + bookId +
            ", rentedDate='" + rentedDate + "'" +
            ", dueDate='" + dueDate + "'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
