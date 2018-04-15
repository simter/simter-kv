package tech.simter.kv.dao.jpa

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import tech.simter.kv.dao.KeyValueDao

private const val MODULE_PACKAGE = "tech.simter.kv.dao.jpa"

/**
 * All configuration for this module.
 *
 * @author RJ
 */
@Configuration("tech.simter.kv.dao.jpa.ModuleConfiguration")
@ComponentScan(MODULE_PACKAGE)
@EnableJpaRepositories(
  basePackages = [MODULE_PACKAGE],
  excludeFilters = [(Filter(type = ASSIGNABLE_TYPE, classes = [KeyValueDao::class]))]
)
@EntityScan(basePackages = ["tech.simter.kv.po"])
class ModuleConfiguration