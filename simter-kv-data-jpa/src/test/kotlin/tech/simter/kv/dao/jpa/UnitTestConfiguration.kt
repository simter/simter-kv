package tech.simter.kv.dao.jpa

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import tech.simter.embeddeddatabase.EmbeddedDatabaseConfiguration

/**
 * @author RJ
 */
@Configuration
@EnableConfigurationProperties
@Import(ModuleConfiguration::class, EmbeddedDatabaseConfiguration::class)
class UnitTestConfiguration