package tech.simter.kv.impl.service

import com.ninjasquad.springmockk.MockkBean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import tech.simter.kv.core.KeyValueDao
import tech.simter.reactive.security.ModuleAuthorizer
import tech.simter.reactive.security.ReactiveSecurityService

/**
 * All test configuration for this module.
 *
 * @author RJ
 */
@Configuration
@Import(ModuleConfiguration::class)
@MockkBean(KeyValueDao::class, ModuleAuthorizer::class, ReactiveSecurityService::class)
class UnitTestConfiguration