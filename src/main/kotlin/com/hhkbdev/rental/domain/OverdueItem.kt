package com.hhkbdev.rental.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import java.time.LocalDate
import javax.persistence.ManyToOne

@Table("overdue_item")
@SuppressWarnings("common-java:DuplicatedBlocks")
data class OverdueItem(
    @Id
    @Column("id")
    var id: Long? = null,

    @Column("book_id")
    var bookId: Long? = null,

    @Column("due_date")
    var dueDate: LocalDate? = null,

    @Column("book_title")
    var bookTitle: String? = null,

    @ManyToOne
    @JsonIgnoreProperties("overdueItems")
    var rental: Rental? = null,
) : Serializable {

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OverdueItem) return false
        return id != null && other.id != null && id == other.id
    }

    override fun toString(): String {
        return "RentedItem{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", dueDate='" + dueDate + "'" +
                "}"
    }

    companion object {
        private const val serialVersionUID = 1L

        fun createOverdueItem(bookId: Long?, bookTitle: String?, dueDate: LocalDate?): OverdueItem {
            return OverdueItem().apply {
                this.bookId = bookId
                this.bookTitle = bookTitle
                this.dueDate = dueDate
            }
        }
    }
}
