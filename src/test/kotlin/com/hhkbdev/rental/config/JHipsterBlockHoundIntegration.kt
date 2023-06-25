package com.hhkbdev.rental.config

import reactor.blockhound.BlockHound
import reactor.blockhound.integration.BlockHoundIntegration

class JHipsterBlockHoundIntegration : BlockHoundIntegration {
    override fun applyTo(builder: BlockHound.Builder) {
        // Workaround until https://github.com/reactor/reactor-core/issues/2137 is fixed
        builder.allowBlockingCallsInside("reactor.core.scheduler.BoundedElasticScheduler\$BoundedState", "dispose")
        builder.allowBlockingCallsInside("reactor.core.scheduler.BoundedElasticScheduler", "schedule")
        builder.allowBlockingCallsInside("org.springframework.validation.beanvalidation.SpringValidatorAdapter", "validate")
        builder.allowBlockingCallsInside("com.hhkbdev.rental.service.MailService", "sendEmailFromTemplate")
        builder.allowBlockingCallsInside("com.hhkbdev.rental.security.DomainUserDetailsService", "createSpringSecurityUser")
    }
}
