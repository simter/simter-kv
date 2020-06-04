package tech.simter.kv.impl.dao.web

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.kotlin.test.test
import tech.simter.kv.core.KeyValueDao
import tech.simter.kv.test.TestHelper
import tech.simter.kv.test.TestHelper.randomKey

/**
 * Test [KeyValueDaoImpl.find].
 *
 * @author RJ
 */
@SpringJUnitConfig(UnitTestConfiguration::class)
@WebFluxTest
class FindMethodImplTest @Autowired constructor(
  private val dao: KeyValueDao
) {
  @Test
  fun `find nothing`() {
    dao.find().test().verifyComplete()
  }

  @Test
  fun `find not exists key`() {
    dao.find(randomKey()).test().verifyComplete()
  }

  @Test
  fun `find exists key`() {
    // prepare data
    val kvs = (1..3).map { TestHelper.randomKeyValue() }
    val map = kvs.associate { it.k to it.v }
    dao.save(map).test().verifyComplete()

    // invoke and verify
    dao.find(*kvs.map { it.k }.toTypedArray())
      .test()
      .assertNext { result ->
        assertEquals(kvs.size, result.size)
        kvs.forEach { assertEquals(it.v, result[it.k]) }
      }
      .verifyComplete()
  }
}