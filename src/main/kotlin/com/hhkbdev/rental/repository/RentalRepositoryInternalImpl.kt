package com.hhkbdev.rental.repository

import com.hhkbdev.rental.domain.Rental
import com.hhkbdev.rental.repository.rowmapper.RentalRowMapper
import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity
import org.springframework.data.relational.core.sql.Condition
import org.springframework.data.relational.core.sql.Conditions
import org.springframework.data.relational.core.sql.Select
import org.springframework.data.relational.core.sql.Table
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.RowsFetchSpec
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Spring Data R2DBC custom repository implementation for the Rental entity.
 */
@SuppressWarnings("unused")
class RentalRepositoryInternalImpl(
    val template: R2dbcEntityTemplate,
    val entityManager: EntityManager,
    val rentalMapper: RentalRowMapper,
    entityOperations: R2dbcEntityOperations,
    converter: R2dbcConverter
) : SimpleR2dbcRepository<Rental, Long>(
    MappingRelationalEntityInformation(
        converter.mappingContext.getRequiredPersistentEntity(Rental::class.java) as RelationalPersistentEntity<Rental>
    ),
    entityOperations,
    converter
),
    RentalRepositoryInternal {

    private val db: DatabaseClient = template.databaseClient

    companion object {
        private val entityTable = Table.aliased("rental", EntityManager.ENTITY_ALIAS)
    }

    override fun findAllBy(pageable: Pageable?): Flux<Rental> {
        return createQuery(pageable, null).all()
    }

    fun createQuery(pageable: Pageable?, whereClause: Condition?): RowsFetchSpec<Rental> {
        val columns = RentalSqlHelper().getColumns(entityTable, EntityManager.ENTITY_ALIAS)
        val selectFrom = Select.builder().select(columns).from(entityTable)

        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        val select = entityManager.createSelect(selectFrom, Rental::class.java, pageable, whereClause)
        return db.sql(select).map(this::process)
    }

    override fun findAll(): Flux<Rental> {
        return findAllBy(null)
    }

    override fun findById(id: Long?): Mono<Rental> {
        val whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()))
        return createQuery(null, whereClause).one()
    }

    private fun process(row: Row, metadata: RowMetadata): Rental {
        val entity = rentalMapper.apply(row, "e")
        return entity
    }

    override fun <S : Rental> save(entity: S): Mono<S> {
        return super.save(entity)
    }
}
