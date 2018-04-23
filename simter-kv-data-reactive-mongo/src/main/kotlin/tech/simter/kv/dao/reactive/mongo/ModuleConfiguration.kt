package tech.simter.kv.dao.reactive.mongo

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.mapping.event.LoggingEventListener
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

private const val MODULE_PACKAGE = "tech.simter.kv.dao.reactive.mongo"

/**
 * All configuration for this module.
 *
 * @author RJ
 */
@Configuration("$MODULE_PACKAGE.ModuleConfiguration")
@EnableReactiveMongoRepositories(MODULE_PACKAGE)
@ComponentScan(MODULE_PACKAGE)
@EntityScan(basePackages = ["tech.simter.kv.po"])
class ModuleConfiguration {
  private val logger = LoggerFactory.getLogger(ModuleConfiguration::class.java)
  @Bean
  @ConditionalOnProperty(name = ["simter.mongodb.enabled-logging-event-listener"], havingValue = "true")
  @ConditionalOnMissingBean
  fun mongoEventListener(): LoggingEventListener {
    logger.warn("instance a LoggingEventListener bean for mongodb operations")
    return LoggingEventListener()
  }
}