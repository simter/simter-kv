package tech.simter.kv.service

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.core.publisher.Mono
import reactor.test.test
import tech.simter.kv.OPERATION_DELETE
import tech.simter.kv.OPERATION_READ
import tech.simter.kv.OPERATION_SAVE
import tech.simter.kv.dao.KeyValueDao
import tech.simter.kv.po.KeyValue
import tech.simter.reactive.security.ModuleAuthorizer
import java.util.*

@SpringJUnitConfig(ModuleConfiguration::class)
@MockkBean(KeyValueDao::class, ModuleAuthorizer::class)
class KeyValueServiceImplTest @Autowired constructor(
  private val moduleAuthorizer: ModuleAuthorizer,
  private val dao: KeyValueDao,
  private val service: KeyValueService
) {
  @Test
  fun getValueSuccess() {
    // mock
    val key = UUID.randomUUID().toString()
    val value = UUID.randomUUID().toString()
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
  fun getValueButKeyNotExists() {
    // mock
    val key = UUID.randomUUID().toString()
    every { dao.valueOf(key) } returns Mono.empty()
    every { moduleAuthorizer.verifyHasPermission(OPERATION_READ) } returns Mono.empty()

    // invoke and verify
    service.valueOf(key).test().expectNext().verifyComplete()
    verify(exactly = 1) { dao.valueOf(key) }
  }

  @Test
  fun findNone() {
    // mock
    every { dao.find(*anyVararg()) } returns Mono.empty()
    every { moduleAuthorizer.verifyHasPermission(OPERATION_READ) } returns Mono.empty()

    // invoke and verify
    service.find().test().expectNext().verifyComplete()
    verify(exactly = 1) { dao.find(*anyVararg()) }
  }

  @Test
  fun findOne() {
    // mock
    val key = UUID.randomUUID().toString()
    val value = UUID.randomUUID().toString()
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
  fun findMulti() {
    // mock
    val kvs = (1..3).map { KeyValue("k-$it", "v-$it") }.associate { it.key to it.value }
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
  fun saveNone() {
    // mock
    val none = mapOf<String, String>()
    every { dao.save(none) } returns Mono.empty()
    every { moduleAuthorizer.verifyHasPermission(OPERATION_SAVE) } returns Mono.empty()

    // invoke and verify
    service.save(none).test().expectNext().verifyComplete()
    verify(exactly = 1) { dao.save(none) }
  }

  @Test
  fun saveOne() {
    // mock
    val kv = mapOf(UUID.randomUUID().toString() to "v")
    every { dao.save(kv) } returns Mono.empty()
    every { moduleAuthorizer.verifyHasPermission(OPERATION_SAVE) } returns Mono.empty()

    // invoke and verify
    service.save(kv).test().expectNext().verifyComplete()
    verify(exactly = 1) { dao.save(kv) }
  }

  @Test
  fun saveMulti() {
    // mock
    val kvs = (1..3).map { KeyValue("k-$it", "v-$it") }.associate { it.key to it.value }
    every { dao.save(kvs) } returns Mono.empty()
    every { moduleAuthorizer.verifyHasPermission(OPERATION_SAVE) } returns Mono.empty()

    // invoke and verify
    service.save(kvs).test().expectNext().verifyComplete()
    verify(exactly = 1) { dao.save(kvs) }
  }

  @Test
  fun deleteOne() {
    // mock
    val key = UUID.randomUUID().toString()
    every { dao.delete(key) } returns Mono.empty()
    every { moduleAuthorizer.verifyHasPermission(OPERATION_DELETE) } returns Mono.empty()

    // invoke and verify
    service.delete(key).test().expectNext().verifyComplete()
    verify(exactly = 1) { dao.delete(key) }
  }

  @Test
  fun deleteMulti() {
    // mock
    val keyList = (1..3).map { "k-$it" }
    val keyArray = keyList.toTypedArray()
    every { dao.delete(*keyArray) } returns Mono.empty()
    every { moduleAuthorizer.verifyHasPermission(OPERATION_DELETE) } returns Mono.empty()

    // invoke and verify
    service.delete(*keyArray).test().expectNext().verifyComplete()
    verify(exactly = 1) { dao.delete(*keyArray) }
  }
}