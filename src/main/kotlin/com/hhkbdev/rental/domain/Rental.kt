package com.hhkbdev.rental.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import javax.validation.constraints.*

/**
 * A Rental.
 */
@Table("rental")
@SuppressWarnings("common-java:DuplicatedBlocks")
data class Rental(

    @Id
    @Column("id")
    var id: Long? = null,

    @get: NotNull(message = "must not be null")
    @get: Size(min = 5, max = 20)
    @Column("title")
    var title: String? = null,
    @Column("description")
    var description: String? = null,

    // jhipster-needle-entity-add-field - JHipster will add fields here
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
            ", title='" + title + "'" +
            ", description='" + description + "'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
