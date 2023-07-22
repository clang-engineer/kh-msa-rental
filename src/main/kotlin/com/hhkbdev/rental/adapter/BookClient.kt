package com.hhkbdev.rental.adapter

import com.hhkbdev.rental.config.FeignConfiguration
import com.hhkbdev.rental.service.dto.BookInfoDTO
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import reactor.core.publisher.Mono

@FeignClient(name = "book", configuration = [FeignConfiguration::class])
interface BookClient {
    @GetMapping("/api/books/bookInfo/{bookId}")
    fun getBookInfo(@PathVariable bookId: Long): Mono<ResponseEntity<BookInfoDTO>>
}