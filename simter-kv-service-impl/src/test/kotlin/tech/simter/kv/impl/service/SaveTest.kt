package tech.simter.kv.impl.service

import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.core.publisher.Mono
import reactor.kotlin.test.test
import tech.simter.exception.PermissionDeniedException
import tech.simter.kv.OPERATION_SAVE
import tech.simter.kv.core.KeyValue
import tech.simter.kv.core.KeyValueDao
import tech.simter.kv.core.KeyValueService
import tech.simter.kv.test.TestHelper.randomKey
import tech.simter.kv.test.TestHelper.randomValue
import tech.simter.reactive.security.ModuleAuthorizer

/**
 * Test [KeyValueServiceImpl.save].
 *
 * @author RJ
 */
@SpringJUnitConfig(UnitTestConfiguration::class)
class SaveTest @Autowired constructor(
  private val moduleAuthorizer: ModuleAuthorizer,
  private val dao: KeyValueDao,
  private val service: KeyValueService
) {
  @Test
  fun `save none`() {
    // mock
    val none = mapOf<String, String>()
    every { dao.save(none) } returns Mono.empty()
    every { moduleAuthorizer.verifyHasPermission(OPERATION_SAVE) } returns Mono.empty()

    // invoke and verify
    service.save(none).test().verifyComplete()
    verify(exactly = 1) { dao.save(none) }
  }

  @Test
  fun `save one`() {
    // mock
    val kv = mapOf(randomKey() to randomValue())
    every { dao.save(kv) } returns Mono.empty()
    every { moduleAuthorizer.verifyHasPermission(OPERATION_SAVE) } returns Mono.empty()

    // invoke and verify
    service.save(kv).test().verifyComplete()
    verify(exactly = 1) { dao.save(kv) }
  }

  @Test
  fun `save multiple`() {
    // mock
    val kvs = (1..3).map { KeyValue.of("k-$it", "v-$it") }.associate { it.k to it.v }
    every { dao.save(kvs) } returns Mono.empty()
    every { moduleAuthorizer.verifyHasPermission(OPERATION_SAVE) } returns Mono.empty()

    // invoke and verify
    service.save(kvs).test().verifyComplete()
    verify(exactly = 1) { dao.save(kvs) }
  }

  @Test
  fun `permission denied`() {
    // mock
    every { moduleAuthorizer.verifyHasPermission(OPERATION_SAVE) } returns Mono.error(PermissionDeniedException())

    // invoke and verify
    service.save(mapOf(randomKey() to randomValue())).test().verifyError(PermissionDeniedException::class.java)
    verify(exactly = 1) { moduleAuthorizer.verifyHasPermission(OPERATION_SAVE) }
  }
}