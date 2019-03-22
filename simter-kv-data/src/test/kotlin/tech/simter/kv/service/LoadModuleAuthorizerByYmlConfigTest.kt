package tech.simter.kv.service

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Mono
import reactor.test.test
import tech.simter.kv.OPERATION_DELETE
import tech.simter.kv.OPERATION_READ
import tech.simter.kv.OPERATION_SAVE
import tech.simter.kv.PACKAGE_NAME
import tech.simter.kv.dao.KeyValueDao
import tech.simter.reactive.security.ModuleAuthorizer
import tech.simter.reactive.security.ReactiveSecurityService
import tech.simter.reactive.security.properties.LogicStrategy
import tech.simter.reactive.security.properties.ModuleAuthorizeProperties
import tech.simter.reactive.security.properties.PermissionStrategy
import java.util.*

/**
 * See `application.yml` key `module.authorization.simter-kv` value.
 */
@SpringBootTest(classes = [ModuleConfiguration::class])
@MockkBean(KeyValueDao::class, ReactiveSecurityService::class)
class LoadModuleAuthorizerByYmlConfigTest @Autowired constructor(
  private val properties: ModuleAuthorizeProperties,
  private val securityService: ReactiveSecurityService,
  @Qualifier("$PACKAGE_NAME.service.ModuleAuthorizer") private val moduleAuthorizer: ModuleAuthorizer,
  private val dao: KeyValueDao,
  private val service: KeyValueService
) {
  @Test
  fun test() {
    assertNotNull(moduleAuthorizer)
    assertEquals(3, properties.operations.size)

    // 1. default permission
    assertEquals(PermissionStrategy.Deny, properties.defaultPermission)

    // 2. not-exists
    assertNull(properties.operations["NONE"])

    // 3. delete
    var operation = properties.operations.getValue(OPERATION_DELETE)
    assertEquals(LogicStrategy.And, operation.strategy)
    assertEquals(listOf("DELETER", "MANAGER"), operation.roles)

    // 4. save
    operation = properties.operations.getValue(OPERATION_SAVE)
    assertEquals(LogicStrategy.Or, operation.strategy)
    assertEquals(listOf("MANAGER"), operation.roles)

    // 5. read
    operation = properties.operations.getValue(OPERATION_READ)
    assertEquals(LogicStrategy.Or, operation.strategy)
    assertEquals(listOf("READER"), operation.roles)

    // 5.1 mock
    val key = UUID.randomUUID().toString()
    val value = UUID.randomUUID().toString()
    every { securityService.verifyHasAnyRole("READER") } returns Mono.empty()
    every { dao.valueOf(key) } returns Mono.just(value)

    // 5.2 invoke and verify
    service.valueOf(key).test().expectNext(value).verifyComplete()
    verify(exactly = 1) {
      securityService.verifyHasAnyRole("READER")
      dao.valueOf(key)
    }
  }
}