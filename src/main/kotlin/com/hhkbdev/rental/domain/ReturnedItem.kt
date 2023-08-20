package com.hhkbdev.rental.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import java.time.LocalDate

@Table("returned_item")
@SuppressWarnings("common-java:DuplicatedBlocks")
data class ReturnedItem(
    @Id
    @Column("id")
    var id: Long? = null,

    @Column("book_id")
    var bookId: Long? = null,

    @Column("returned_date")
    var returnedDate: LocalDate? = null,

    @Column("book_title")
    var bookTitle: String? = null,

) : Serializable {
    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ReturnedItem) return false
        return id != null && other.id != null && id == other.id
    }

    override fun toString(): String {
        return "RentedItem{" +
            "id=" + id +
            ", bookId=" + bookId +
            ", returnedDate='" + returnedDate + "'" +
            ", bookTitle='" + bookTitle + "'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L

        fun createReturnedItem(bookId: Long?, bookTitle: String?, returnedDate: LocalDate): ReturnedItem {
            return ReturnedItem(bookId = bookId, bookTitle = bookTitle, returnedDate = returnedDate)
        }
    }
}
