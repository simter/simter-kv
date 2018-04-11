package tech.simter.kv.dao.jpa

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import tech.simter.kv.dao.KeyValueDao

/**
 * @author RJ
 */
@Configuration
@ComponentScan("tech.simter.kv")
@EnableJpaRepositories(excludeFilters = [Filter(type = ASSIGNABLE_TYPE, classes = [KeyValueDao::class])])
@EntityScan(basePackages = ["tech.simter.kv"])
class JpaTestConfiguration