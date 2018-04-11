package tech.simter.kv.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import tech.simter.kv.dao.KeyValueDao
import tech.simter.kv.po.KeyValue
import java.util.*
import kotlin.test.assertNotNull

@SpringJUnitConfig(KeyValueServiceImpl::class)
@MockBean(KeyValueDao::class)
class KeyValueServiceImplTest @Autowired constructor(
  private val dao: KeyValueDao,
  private val service: KeyValueService
) {
  @Test
  fun getValue() {
    // mock
    val kv = KeyValue(UUID.randomUUID().toString(), "value")
    val expected = Mono.just(kv)
    `when`(dao.findById(kv.key)).thenReturn(expected)

    // invoke
    val actual = service.getValue(kv.key)

    // verify
    StepVerifier.create(actual)
      .expectNext(kv.value!!)
      .verifyComplete()
    verify(dao).findById(kv.key)
  }

  @Test
  fun findAll() {
    // mock
    val range = (1..3)
    val kvs = range.map { KeyValue("k-$it", "v-$it") }
    val expected = Flux.fromIterable(kvs)
    `when`(dao.findAllById(anyIterable())).thenReturn(expected)

    // invoke
    val actual = service.findAll(*range.map { "k-$it" }.toTypedArray())
    assertNotNull(actual.collectList().block())

    // verify
    StepVerifier.create(actual)
      .recordWith({ ArrayList() })
      .expectNextCount(kvs.size.toLong())
      .consumeRecordedWith { for ((index, value) in it.withIndex()) assertEquals(kvs[index], value) }
      .verifyComplete()
    verify(dao).findAllById(anyIterable())
  }

  @Test
  fun saveOne() {
    // mock
    val kv = KeyValue(UUID.randomUUID().toString(), "value")
    val expected = Mono.just(kv)
    `when`(dao.save(kv)).thenReturn(expected)

    // invoke
    val actual = service.save(kv)

    // verify
    StepVerifier.create(actual).expectComplete()
    verify(dao).save(kv)
  }

  @Test
  fun saveAll() {
    // mock
    val pos = Flux.fromIterable((1..3 step 1).asIterable())
      .map { KeyValue("key$it", "value$it") }
    `when`(dao.saveAll(pos)).thenReturn(pos)

    // invoke
    val actual = service.saveAll(pos)

    // verify
    StepVerifier.create(actual).expectComplete()
    verify(dao).saveAll(pos)
  }
}