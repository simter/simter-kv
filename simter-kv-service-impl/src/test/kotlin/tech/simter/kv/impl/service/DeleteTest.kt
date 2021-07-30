package tech.simter.kv.impl.service

import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.core.publisher.Mono
import reactor.kotlin.test.test
import tech.simter.exception.PermissionDeniedException
import tech.simter.kv.OPERATION_DELETE
import tech.simter.kv.core.KeyValueDao
import tech.simter.kv.core.KeyValueService
import tech.simter.kv.test.TestHelper.randomKey
import tech.simter.reactive.security.ModuleAuthorizer

/**
 * Test [KeyValueServiceImpl.delete].
 *
 * @author RJ
 */
@SpringJUnitConfig(UnitTestConfiguration::class)
class DeleteTest @Autowired constructor(
  private val moduleAuthorizer: ModuleAuthorizer,
  private val dao: KeyValueDao,
  private val service: KeyValueService
) {
  @Test
  fun `delete one`() {
    // mock
    val key = randomKey()
    every { dao.delete(key) } returns Mono.empty()
    every { moduleAuthorizer.verifyHasPermission(OPERATION_DELETE) } returns Mono.empty()

    // invoke and verify
    service.delete(key).test().verifyComplete()
    verify(exactly = 1) { dao.delete(key) }
  }

  @Test
  fun `delete multiple`() {
    // mock
    val keyList = (1..3).map { "k-$it" }
    val keyArray = keyList.toTypedArray()
    every { dao.delete(*keyArray) } returns Mono.empty()
    every { moduleAuthorizer.verifyHasPermission(OPERATION_DELETE) } returns Mono.empty()

    // invoke and verify
    service.delete(*keyArray).test().expectNext().verifyComplete()
    verify(exactly = 1) { dao.delete(*keyArray) }
  }

  @Test
  fun `permission denied`() {
    // mock
    every { moduleAuthorizer.verifyHasPermission(OPERATION_DELETE) } returns Mono.error(PermissionDeniedException())

    // invoke and verify
    service.delete(randomKey()).test().verifyError(PermissionDeniedException::class.java)
    verify(exactly = 1) { moduleAuthorizer.verifyHasPermission(OPERATION_DELETE) }
  }
}