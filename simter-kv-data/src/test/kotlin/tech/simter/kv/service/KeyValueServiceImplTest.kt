package tech.simter.kv.service

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.times
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyIterable
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.core.publisher.Flux
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
    val kv = KeyValue(UUID.randomUUID().toString(), "v")
    val expected = Mono.just(kv)
    `when`(dao.findById(kv.key)).thenReturn(expected)

    // invoke
    val actual = service.getValue(kv.key)

    // verify
    StepVerifier.create(actual).expectNext(kv.value).verifyComplete()
    verify(dao).findById(kv.key)
  }

  @Test
  fun getValueButKeyNotExists() {
    // mock
    val key = UUID.randomUUID().toString()
    `when`(dao.findById(key)).thenReturn(Mono.empty())

    // invoke
    val actual = service.getValue(key)

    // verify
    StepVerifier.create(actual).expectNext().verifyComplete()
    verify(dao).findById(key)
  }

  @Test
  fun findNone() {
    // invoke
    val actual = service.find()

    // verify
    StepVerifier.create(actual).expectNext().verifyComplete()
    verify(dao, times(0)).findById(any())
    verify(dao, times(0)).findAllById(any())
  }

  @Test
  fun findOne() {
    // mock
    val expected = KeyValue(UUID.randomUUID().toString(), "v")
    `when`(dao.findById(expected.key)).thenReturn(Mono.just(expected))

    // invoke
    val actual = service.find(expected.key)

    // verify
    StepVerifier.create(actual)
      .consumeNextWith {
        assertEquals(1, it.size)
        val first = it.entries.first()
        assertEquals(expected.key, first.key)
        assertEquals(expected.value, first.value)
      }
      .verifyComplete()
    verify(dao).findById(expected.key)
  }

  @Test
  fun findMulti() {
    // mock
    val expected = (1..3).map { KeyValue("k-$it", "v-$it") }
    `when`(dao.findAllById(anyIterable())).thenReturn(Flux.fromIterable(expected))

    // invoke
    val actual = service.find(*expected.map { it.key }.toTypedArray())

    // verify
    StepVerifier.create(actual)
      .consumeNextWith { actualMap ->
        assertEquals(expected.size, actualMap.size)
        expected.associate { it.key to it.value }.forEach { assertEquals(it.value, actualMap[it.key]) }
      }
      .verifyComplete()
    verify(dao).findAllById(anyIterable())
  }

  @Test
  fun saveNone() {
    // invoke
    val actual = service.save(mapOf())

    // verify
    StepVerifier.create(actual).expectNext().verifyComplete()
    verify(dao, times(0)).save(any())
    verify(dao, times(0)).saveAll(any())
  }

  @Test
  fun saveOne() {
    // mock
    val kv = mapOf(UUID.randomUUID().toString() to "v")
    val expected = Mono.just(kv).map { KeyValue(it.entries.first().key, it.entries.first().value) }
    `when`(dao.save(any())).thenReturn(expected)

    // invoke
    val actual = service.save(kv)

    // verify
    StepVerifier.create(actual).expectNext().verifyComplete()
    verify(dao).save(any())
  }

  @Test
  fun saveMulti() {
    // mock
    val kvs = Flux.fromIterable((1..3).map { KeyValue("k-$it", "v-$it") })
    `when`(dao.saveAll(any())).thenReturn(kvs)

    // invoke
    val actual = service.save(kvs.collectMap({ it.key }, { it.value }).block()!!)

    // verify
    StepVerifier.create(actual).expectNext().verifyComplete()
    verify(dao).saveAll(any())
  }
}