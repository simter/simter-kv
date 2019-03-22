package tech.simter.kv.service

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import tech.simter.reactive.security.ModuleAuthorizer
import tech.simter.reactive.security.ReactiveSecurityService
import tech.simter.reactive.security.properties.ModuleAuthorizeProperties
import tech.simter.reactive.security.properties.PermissionStrategy.Allow

internal const val MODULE_PACKAGE = "tech.simter.kv.service"

/**
 * All configuration for this module.
 *
 * @author RJ
 */
@Configuration("$MODULE_PACKAGE.ModuleConfiguration")
@EnableConfigurationProperties
@ComponentScan(MODULE_PACKAGE)
class ModuleConfiguration {
  /**
   * Starter should config yml key `module.authorization.simter-kv` to support specific role control,
   * otherwise the [ModuleConfiguration.moduleAuthorizer] would allow anything default.
   */
  @Bean("$MODULE_PACKAGE.ModuleAuthorizeProperties")
  @ConfigurationProperties(prefix = "module.authorization.simter-kv")
  fun moduleAuthorizeProperties(): ModuleAuthorizeProperties {
    return ModuleAuthorizeProperties(defaultPermission = Allow)
  }

  @Bean("$MODULE_PACKAGE.ModuleAuthorizer")
  fun moduleAuthorizer(
    @Qualifier("$MODULE_PACKAGE.ModuleAuthorizeProperties")
    properties: ModuleAuthorizeProperties,
    securityService: ReactiveSecurityService
  ): ModuleAuthorizer {
    return ModuleAuthorizer.create(properties, securityService)
  }
}