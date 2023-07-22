package com.hhkbdev.rental.config

import com.hhkbdev.rental.web.rest.errors.FeignClientExceptionErrorDecoder
import feign.codec.ErrorDecoder
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@EnableFeignClients(basePackages = ["com.hhkbdev.rental"])
@Import(FeignClientConfiguration::class)
class FeignConfiguration {

    @Bean
    fun feignLoggerLevel(): feign.Logger.Level {
        return feign.Logger.Level.BASIC
    }

    @Bean
    @ConditionalOnMissingBean(ErrorDecoder::class)
    fun commonFeignErrorDecoder(): ErrorDecoder {
        return FeignClientExceptionErrorDecoder()
    }
}