package tech.simter.kv.impl.service

import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.core.publisher.Mono
import reactor.kotlin.test.test
import tech.simter.exception.PermissionDeniedException
import tech.simter.kv.OPERATION_READ
import tech.simter.kv.core.KeyValue
import tech.simter.kv.core.KeyValueDao
import tech.simter.kv.core.KeyValueService
import tech.simter.kv.test.TestHelper.randomKey
import tech.simter.kv.test.TestHelper.randomValue
import tech.simter.reactive.security.ModuleAuthorizer

/**
 * Test [KeyValueServiceImpl.find].
 *
 * @author RJ
 */
@SpringJUnitConfig(UnitTestConfiguration::class)
class FindMethodImplTest @Autowired constructor(
  private val moduleAuthorizer: ModuleAuthorizer,
  private val dao: KeyValueDao,
  private val service: KeyValueService
) {
  @Test
  fun `found nothing`() {
    // mock
    every { dao.find(*anyVararg()) } returns Mono.empty()
    every { moduleAuthorizer.verifyHasPermission(OPERATION_READ) } returns Mono.empty()

    // invoke and verify
    service.find().test().verifyComplete()
    verify(exactly = 1) { dao.find(*anyVararg()) }
  }

  @Test
  fun `found one`() {
    // mock
    val key = randomKey()
    val value = randomValue()
    val expected = mapOf(key to value)
    every { dao.find(key) } returns Mono.just(expected)
    every { moduleAuthorizer.verifyHasPermission(OPERATION_READ) } returns Mono.empty()

    // invoke and verify
    service.find(key)
      .test()
      .consumeNextWith {
        assertEquals(1, it.size)
        val first = it.entries.first()
        assertEquals(key, first.key)
        assertEquals(value, first.value)
      }.verifyComplete()
    verify(exactly = 1) { dao.find(key) }
  }

  @Test
  fun `found multiple`() {
    // mock
    val kvs = (1..3).map { KeyValue.of("k-$it", "v-$it") }.associate { it.k to it.v }
    val expected = Mono.just(kvs)
    every { dao.find(*anyVararg()) } returns expected
    every { moduleAuthorizer.verifyHasPermission(OPERATION_READ) } returns Mono.empty()

    // invoke and verify
    service.find(*kvs.keys.toTypedArray())
      .test()
      .consumeNextWith { actualMap ->
        assertEquals(kvs.size, actualMap.size)
        kvs.forEach { assertEquals(it.value, actualMap[it.key]) }
      }.verifyComplete()
    verify(exactly = 1) { dao.find(*anyVararg()) }
  }

  @Test
  fun `permission denied`() {
    // mock
    every { moduleAuthorizer.verifyHasPermission(OPERATION_READ) } returns Mono.error(PermissionDeniedException())

    // invoke and verify
    service.find(randomKey()).test().verifyError(PermissionDeniedException::class.java)
    verify(exactly = 1) { moduleAuthorizer.verifyHasPermission(OPERATION_READ) }
  }
}