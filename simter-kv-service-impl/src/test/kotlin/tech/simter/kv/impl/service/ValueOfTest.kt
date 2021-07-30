package tech.simter.kv.impl.service

import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.core.publisher.Mono
import reactor.kotlin.test.test
import tech.simter.exception.PermissionDeniedException
import tech.simter.kv.OPERATION_READ
import tech.simter.kv.core.KeyValueDao
import tech.simter.kv.core.KeyValueService
import tech.simter.kv.test.TestHelper.randomKey
import tech.simter.kv.test.TestHelper.randomValue
import tech.simter.reactive.security.ModuleAuthorizer

/**
 * Test [KeyValueServiceImpl.valueOf].
 *
 * @author RJ
 */
@SpringJUnitConfig(UnitTestConfiguration::class)
class ValueOfTest @Autowired constructor(
  private val moduleAuthorizer: ModuleAuthorizer,
  private val dao: KeyValueDao,
  private val service: KeyValueService
) {
  @Test
  fun `get it`() {
    // mock
    val key = randomKey()
    val value = randomValue()
    val expected = Mono.just(value)
    every { dao.valueOf(key) } returns expected
    every { moduleAuthorizer.verifyHasPermission(OPERATION_READ) } returns Mono.empty()

    // invoke and verify
    service.valueOf(key).test().expectNext(value).verifyComplete()
    verify(exactly = 1) {
      moduleAuthorizer.verifyHasPermission(OPERATION_READ)
      dao.valueOf(key)
    }
  }

  @Test
  fun `key not exists`() {
    // mock
    val key = randomKey()
    every { dao.valueOf(key) } returns Mono.empty()
    every { moduleAuthorizer.verifyHasPermission(OPERATION_READ) } returns Mono.empty()

    // invoke and verify
    service.valueOf(key).test().verifyComplete()
    verify(exactly = 1) { dao.valueOf(key) }
  }

  @Test
  fun `permission denied`() {
    // mock
    every { moduleAuthorizer.verifyHasPermission(OPERATION_READ) } returns Mono.error(PermissionDeniedException())

    // invoke and verify
    service.valueOf(randomKey()).test().verifyError(PermissionDeniedException::class.java)
    verify(exactly = 1) { moduleAuthorizer.verifyHasPermission(OPERATION_READ) }
  }
}