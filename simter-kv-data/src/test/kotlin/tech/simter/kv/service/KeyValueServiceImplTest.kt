package tech.simter.kv.service

import com.nhaarman.mockito_kotlin.any
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import tech.simter.kv.dao.KeyValueDao
import tech.simter.kv.po.KeyValue
import java.util.*

@SpringJUnitConfig(KeyValueServiceImpl::class)
@MockBean(KeyValueDao::class)
class KeyValueServiceImplTest @Autowired constructor(
  private val dao: KeyValueDao,
  private val service: KeyValueService
) {
  @Test
  fun getValueSuccess() {
    // mock
    val key = UUID.randomUUID().toString()
    val value = UUID.randomUUID().toString()
    val expected = Mono.just(value)
    `when`(dao.valueOf(key)).thenReturn(expected)

    // invoke
    val actual = service.valueOf(key)

    // verify
    StepVerifier.create(actual).expectNext(value).verifyComplete()
    verify(dao).valueOf(key)
  }

  @Test
  fun getValueButKeyNotExists() {
    // mock
    val key = UUID.randomUUID().toString()
    `when`(dao.valueOf(key)).thenReturn(Mono.empty())

    // invoke
    val actual = service.valueOf(key)

    // verify
    StepVerifier.create(actual).expectNext().verifyComplete()
    verify(dao).valueOf(key)
  }

  @Test
  fun findNone() {
    // mock
    `when`(dao.find(any())).thenReturn(Mono.empty())

    // invoke
    val actual = service.find()

    // verify
    StepVerifier.create(actual).expectNext().verifyComplete()
    verify(dao).find(any())
  }

  @Test
  fun findOne() {
    // mock
    val key = UUID.randomUUID().toString()
    val value = UUID.randomUUID().toString()
    val expected = mapOf(key to value)
    `when`(dao.find(key)).thenReturn(Mono.just(expected))

    // invoke
    val actual = service.find(key)

    // verify
    StepVerifier.create(actual)
      .consumeNextWith {
        assertEquals(1, it.size)
        val first = it.entries.first()
        assertEquals(key, first.key)
        assertEquals(value, first.value)
      }
      .verifyComplete()
    verify(dao).find(key)
  }

  @Test
  fun findMulti() {
    // mock
    val kvs = (1..3).map { KeyValue("k-$it", "v-$it") }.associate { it.key to it.value }
    val expected = Mono.just(kvs)
    `when`(dao.find(any())).thenReturn(expected)

    // invoke
    val actual = service.find(*kvs.keys.toTypedArray())

    // verify
    StepVerifier.create(actual)
      .consumeNextWith { actualMap ->
        assertEquals(kvs.size, actualMap.size)
        kvs.forEach { assertEquals(it.value, actualMap[it.key]) }
      }
      .verifyComplete()
    verify(dao).find(any())
  }

  @Test
  fun saveNone() {
    // mock
    val none = mapOf<String, String>()
    `when`(dao.save(none)).thenReturn(Mono.empty())

    // invoke
    val actual = service.save(none)

    // verify
    StepVerifier.create(actual).expectNext().verifyComplete()
    verify(dao).save(none)
  }

  @Test
  fun saveOne() {
    // mock
    val kv = mapOf(UUID.randomUUID().toString() to "v")
    `when`(dao.save(kv)).thenReturn(Mono.empty())

    // invoke
    val actual = service.save(kv)

    // verify
    StepVerifier.create(actual).expectNext().verifyComplete()
    verify(dao).save(kv)
  }

  @Test
  fun saveMulti() {
    // mock
    val kvs = (1..3).map { KeyValue("k-$it", "v-$it") }.associate { it.key to it.value }
    `when`(dao.save(kvs)).thenReturn(Mono.empty())

    // invoke
    val actual = service.save(kvs)

    // verify
    StepVerifier.create(actual).expectNext().verifyComplete()
    verify(dao).save(kvs)
  }

  @Test
  fun deleteOne() {
    // mock
    val key = UUID.randomUUID().toString()
    `when`(dao.delete(key)).thenReturn(Mono.empty())

    // invoke
    val actual = service.delete(key)

    // verify
    StepVerifier.create(actual).expectNext().verifyComplete()
    verify(dao).delete(key)
  }

  @Test
  fun deleteMulti() {
    // mock
    val keyList = (1..3).map { "k-$it" }
    val keyArray = keyList.toTypedArray()
    `when`(dao.delete(*keyArray)).thenReturn(Mono.empty())

    // invoke
    val actual = service.delete(*keyArray)

    // verify
    StepVerifier.create(actual).expectNext().verifyComplete()
    verify(dao).delete(*keyArray)
  }
}