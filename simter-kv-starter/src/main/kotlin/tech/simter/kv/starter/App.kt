package tech.simter.kv.starter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import tech.simter.kv.dao.KeyValueDao

private const val KV_BASE_PACKAGE = "tech.simter.kv"

@SpringBootApplication(
  scanBasePackages = [KV_BASE_PACKAGE],
  scanBasePackageClasses = [ProjectInfoAutoConfiguration::class]
)
@EnableJpaRepositories(basePackages = [KV_BASE_PACKAGE], excludeFilters = [(Filter(type = ASSIGNABLE_TYPE, classes = [KeyValueDao::class]))])
@EntityScan(basePackages = [KV_BASE_PACKAGE])
class App

fun main(args: Array<String>) {
  runApplication<App>(*args)
}