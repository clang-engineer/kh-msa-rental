package com.hhkbdev.rental.service.impl

import com.hhkbdev.rental.domain.RentedItem
import com.hhkbdev.rental.repository.RentedItemRepository
import com.hhkbdev.rental.service.RentedItemService
import com.hhkbdev.rental.service.dto.RentedItemDTO
import com.hhkbdev.rental.service.mapper.RentedItemMapper
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service Implementation for managing [RentedItem].
 */
@Service
@Transactional
class RentedItemServiceImpl(
    private val rentedItemRepository: RentedItemRepository,
    private val rentedItemMapper: RentedItemMapper,
) : RentedItemService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun save(rentedItemDTO: RentedItemDTO): Mono<RentedItemDTO> {
        log.debug("Request to save RentedItem : $rentedItemDTO")
        return rentedItemRepository.save(rentedItemMapper.toEntity(rentedItemDTO))
            .map(rentedItemMapper::toDto)
    }

    override fun update(rentedItemDTO: RentedItemDTO): Mono<RentedItemDTO> {
        log.debug("Request to update RentedItem : {}", rentedItemDTO)
        return rentedItemRepository.save(rentedItemMapper.toEntity(rentedItemDTO))
            .map(rentedItemMapper::toDto)
    }

    override fun partialUpdate(rentedItemDTO: RentedItemDTO): Mono<RentedItemDTO> {
        log.debug("Request to partially update RentedItem : {}", rentedItemDTO)

        return rentedItemRepository.findById(rentedItemDTO.id)
            .map {
                rentedItemMapper.partialUpdate(it, rentedItemDTO)
                it
            }
            .flatMap { rentedItemRepository.save(it) }
            .map { rentedItemMapper.toDto(it) }
    }

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Flux<RentedItemDTO> {
        log.debug("Request to get all RentedItems")
        return rentedItemRepository.findAllBy(pageable)
            .map(rentedItemMapper::toDto)
    }

    override fun countAll() = rentedItemRepository.count()

    @Transactional(readOnly = true)
    override fun findOne(id: Long): Mono<RentedItemDTO> {
        log.debug("Request to get RentedItem : $id")
        return rentedItemRepository.findById(id)
            .map(rentedItemMapper::toDto)
    }

    override fun delete(id: Long): Mono<Void> {
        log.debug("Request to delete RentedItem : $id")
        return rentedItemRepository.deleteById(id)
    }
}
