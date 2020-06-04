package tech.simter.kv.impl.dao.r2dbc

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.kotlin.test.test
import tech.simter.kv.core.KeyValueDao
import tech.simter.kv.test.TestHelper.randomKey

/**
 * Test [KeyValueDaoImpl.find].
 *
 * @author RJ
 */
@SpringJUnitConfig(UnitTestConfiguration::class)
@DataR2dbcTest
class FindMethodImplTest @Autowired constructor(
  private val repository: KeyValueRepository,
  private val dao: KeyValueDao
) {
  @Test
  fun `find nothing`() {
    repository.deleteAll().block()
    dao.find().test().verifyComplete()
  }

  @Test
  fun `find not exists key`() {
    dao.find(randomKey()).test().verifyComplete()
  }

  @Test
  fun `find exists key`() {
    // prepare data
    val pos = (1..3).map { repository.save(TestHelper.randomKeyValuePo()).block()!! }

    // invoke and verify
    dao.find(*pos.map { it.k }.toTypedArray())
      .test()
      .assertNext { result ->
        assertEquals(pos.size, result.size)
        pos.forEach { assertEquals(it.v, result[it.k]) }
      }
      .verifyComplete()
  }
}