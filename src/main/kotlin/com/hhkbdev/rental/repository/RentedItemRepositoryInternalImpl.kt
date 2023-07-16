package com.hhkbdev.rental.repository

import com.hhkbdev.rental.domain.RentedItem
import com.hhkbdev.rental.repository.rowmapper.RentalRowMapper
import com.hhkbdev.rental.repository.rowmapper.RentedItemRowMapper
import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity
import org.springframework.data.relational.core.sql.Column
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
 * Spring Data R2DBC custom repository implementation for the RentedItem entity.
 */
@SuppressWarnings("unused")
class RentedItemRepositoryInternalImpl(
    val template: R2dbcEntityTemplate,
    val entityManager: EntityManager,
    val rentalMapper: RentalRowMapper,

    val renteditemMapper: RentedItemRowMapper,
    entityOperations: R2dbcEntityOperations,
    converter: R2dbcConverter
) : SimpleR2dbcRepository<RentedItem, Long>(
    MappingRelationalEntityInformation(
        converter.mappingContext.getRequiredPersistentEntity(RentedItem::class.java) as RelationalPersistentEntity<RentedItem>
    ),
    entityOperations,
    converter
),
    RentedItemRepositoryInternal {

    private val db: DatabaseClient = template.databaseClient

    companion object {
        private val entityTable = Table.aliased("rented_item", EntityManager.ENTITY_ALIAS)
        private val rentalTable = Table.aliased("rental", "rental")
    }

    override fun findAllBy(pageable: Pageable?): Flux<RentedItem> {
        return createQuery(pageable, null).all()
    }

    fun createQuery(pageable: Pageable?, whereClause: Condition?): RowsFetchSpec<RentedItem> {
        val columns = RentedItemSqlHelper().getColumns(entityTable, EntityManager.ENTITY_ALIAS)
        columns.addAll(RentalSqlHelper().getColumns(rentalTable, "rental"))
        val selectFrom = Select.builder().select(columns).from(entityTable)
            .leftOuterJoin(rentalTable).on(Column.create("rental_id", entityTable)).equals(Column.create("id", rentalTable))

        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        val select = entityManager.createSelect(selectFrom, RentedItem::class.java, pageable, whereClause)
        return db.sql(select).map(this::process)
    }

    override fun findAll(): Flux<RentedItem> {
        return findAllBy(null)
    }

    override fun findById(id: Long?): Mono<RentedItem> {
        val whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()))
        return createQuery(null, whereClause).one()
    }

    private fun process(row: Row, metadata: RowMetadata): RentedItem {
        val entity = renteditemMapper.apply(row, "e")
        entity.rental = rentalMapper.apply(row, "rental")
        return entity
    }

    override fun <S : RentedItem> save(entity: S): Mono<S> {
        return super.save(entity)
    }
}
