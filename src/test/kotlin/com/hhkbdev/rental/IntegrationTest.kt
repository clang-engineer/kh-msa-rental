package com.hhkbdev.rental

import com.hhkbdev.rental.config.AsyncSyncConfiguration
import com.hhkbdev.rental.config.EmbeddedKafka
import com.hhkbdev.rental.config.EmbeddedSQL
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext

/**
 * Base composite annotation for integration tests.
 */
@kotlin.annotation.Target(AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@SpringBootTest(classes = [RentalApp::class, AsyncSyncConfiguration::class])
@EmbeddedKafka
@EmbeddedSQL
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
annotation class IntegrationTest {
    companion object {
        // 5s is the spring default https://github.com/spring-projects/spring-framework/blob/29185a3d28fa5e9c1b4821ffe519ef6f56b51962/spring-test/src/main/java/org/springframework/test/web/reactive/server/DefaultWebTestClient.java#L106
        const val DEFAULT_TIMEOUT: String = "PT5S"
        const val DEFAULT_ENTITY_TIMEOUT: String = "PT5S"
    }
}
