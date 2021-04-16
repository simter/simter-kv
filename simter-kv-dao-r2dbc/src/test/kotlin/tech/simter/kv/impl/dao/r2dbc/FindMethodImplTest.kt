package tech.simter.kv.impl.dao.r2dbc

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.kotlin.test.test
import tech.simter.kv.core.KeyValueDao
import tech.simter.kv.impl.dao.r2dbc.TestHelper.randomKeyValuePo
import tech.simter.kv.impl.dao.r2dbc.po.KeyValuePo
import tech.simter.kv.test.TestHelper.randomKey
import tech.simter.kv.test.TestHelper.randomValue
import tech.simter.util.RandomUtils.randomString

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
  fun `find single key by equation match`() {
    // prepare data
    val po = repository.save(randomKeyValuePo()).block()!!

    // invoke and verify
    dao.find(po.k)
      .test()
      .assertNext { result ->
        assertEquals(1, result.size)
        assertTrue(result.containsKey(po.k))
        assertEquals(po.v, result[po.k])
      }
      .verifyComplete()
  }

  @Test
  fun `find single key by fuzzy match`() {
    // prepare data
    val prefix = randomString(6)
    val pos = (1..3).map { repository.save(KeyValuePo(k = "$prefix.$it-${randomKey()}", v = randomValue())).block()!! }

    // invoke and verify
    dao.find("$prefix.%")
      .test()
      .assertNext { result ->
        assertEquals(pos.size, result.size)
        pos.forEach {
          assertTrue(result.containsKey(it.k))
          assertEquals(it.v, result[it.k])
        }
      }
      .verifyComplete()
  }

  @Test
  fun `find multiple keys by equation match`() {
    // prepare data
    val pos = (1..3).map { repository.save(randomKeyValuePo()).block()!! }

    // invoke and verify
    dao.find(*pos.map { it.k }.toTypedArray())
      .test()
      .assertNext { result ->
        assertEquals(pos.size, result.size)
        pos.forEach {
          assertTrue(result.containsKey(it.k))
          assertEquals(it.v, result[it.k])
        }
      }
      .verifyComplete()
  }

  @Test
  fun `find multiple keys by fuzzy match`() {
    // prepare data
    val prefix1 = randomString(6)
    val prefix2 = randomString(6)
    val pos1 = (1..3).map { repository.save(KeyValuePo(k = "$prefix1.$it-${randomKey()}", v = randomValue())).block()!! }
    val pos2 = (1..3).map { repository.save(KeyValuePo(k = "$prefix2.$it-${randomKey()}", v = randomValue())).block()!! }

    // invoke and verify
    dao.find("$prefix1.%", "$prefix2.%")
      .test()
      .assertNext { result ->
        assertEquals(pos1.size + pos2.size, result.size)
        pos1.forEach {
          assertTrue(result.containsKey(it.k))
          assertEquals(it.v, result[it.k])
        }
        pos2.forEach {
          assertTrue(result.containsKey(it.k))
          assertEquals(it.v, result[it.k])
        }
      }
      .verifyComplete()
  }

  @Test
  fun `find multiple keys by equation match and fuzzy match`() {
    // prepare data
    val prefix1 = randomString(6)
    val prefix2 = randomString(6)
    repository.save(randomKeyValuePo()).block()!!
    val po1 = repository.save(randomKeyValuePo()).block()!!
    val po2 = repository.save(KeyValuePo(k = "$prefix1.1-${randomKey()}", v = randomValue())).block()!!
    val po3 = repository.save(KeyValuePo(k = "$prefix1.2-${randomKey()}", v = randomValue())).block()!!
    val po4 = repository.save(KeyValuePo(k = "$prefix2.1-${randomKey()}", v = randomValue())).block()!!
    val po5 = repository.save(KeyValuePo(k = "$prefix2.2-${randomKey()}", v = randomValue())).block()!!

    // invoke and verify
    dao.find(po1.k, "$prefix1.%", "$prefix2.%")
      .test()
      .assertNext { result ->
        assertEquals(5, result.size)
        assertTrue(result.containsKey(po1.k))
        assertEquals(po1.v, result[po1.k])

        assertTrue(result.containsKey(po2.k))
        assertEquals(po2.v, result[po2.k])

        assertTrue(result.containsKey(po3.k))
        assertEquals(po3.v, result[po3.k])

        assertTrue(result.containsKey(po4.k))
        assertEquals(po4.v, result[po4.k])

        assertTrue(result.containsKey(po5.k))
        assertEquals(po5.v, result[po5.k])
      }
      .verifyComplete()
  }
}