package com.hhkbdev.rental.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import java.time.LocalDate
import javax.persistence.ManyToOne

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

    @Column("book_title")
    var bookTitle: String? = null,

    @ManyToOne
    @JsonIgnoreProperties("rentedItems")
    var rental: Rental? = null,
) : Serializable {
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

        fun createRentedItem(bookId: Long?, bookTitle: String?, rentedDate: LocalDate): RentedItem {
            return RentedItem(bookId = bookId, bookTitle = bookTitle, rentedDate = rentedDate, dueDate = rentedDate.plusWeeks(2))
        }
    }
}
