package tech.simter.kv.service

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import tech.simter.kv.PACKAGE_NAME
import tech.simter.reactive.security.ModuleAuthorizer
import tech.simter.reactive.security.ReactiveSecurityService
import tech.simter.reactive.security.properties.ModuleAuthorizeProperties
import tech.simter.reactive.security.properties.PermissionStrategy.Allow

/**
 * All configuration for this module.
 *
 * @author RJ
 */
@Configuration("$PACKAGE_NAME.service.ModuleConfiguration")
@EnableConfigurationProperties
@ComponentScan("$PACKAGE_NAME.service")
class ModuleConfiguration {
  /**
   * Starter should config yml key `module.authorization.simter-kv` to support specific role control,
   * otherwise the [ModuleConfiguration.moduleAuthorizer] would allow anything default.
   */
  @Bean("$PACKAGE_NAME.service.ModuleAuthorizeProperties")
  @ConfigurationProperties(prefix = "module.authorization.simter-kv")
  fun moduleAuthorizeProperties(): ModuleAuthorizeProperties {
    return ModuleAuthorizeProperties(defaultPermission = Allow)
  }

  @Bean("$PACKAGE_NAME.service.ModuleAuthorizer")
  fun moduleAuthorizer(
    @Qualifier("$PACKAGE_NAME.service.ModuleAuthorizeProperties")
    properties: ModuleAuthorizeProperties,
    securityService: ReactiveSecurityService
  ): ModuleAuthorizer {
    return ModuleAuthorizer.create(properties, securityService)
  }
}