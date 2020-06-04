package tech.simter.kv.impl.dao.r2dbc

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.kotlin.test.test
import tech.simter.kv.TABLE_KV
import tech.simter.kv.core.KeyValueDao
import tech.simter.kv.impl.dao.r2dbc.TestHelper.randomKeyValuePo
import tech.simter.kv.impl.dao.r2dbc.po.KeyValuePo
import tech.simter.kv.test.TestHelper.randomValue
import tech.simter.util.RandomUtils.randomString

/**
 * Test [KeyValueDaoImpl.save].
 *
 * @author RJ
 */
@SpringJUnitConfig(UnitTestConfiguration::class)
@DataR2dbcTest
class SaveMethodImplTest @Autowired constructor(
  private val databaseClient: DatabaseClient,
  private val repository: KeyValueRepository,
  private val dao: KeyValueDao
) {
  @Test
  fun `save nothing`() {
    dao.save(emptyMap()).test().verifyComplete()
  }

  @Test
  fun `create one`() {
    // do save
    val po = randomKeyValuePo()
    dao.save(mapOf(po.k to po.v)).test().verifyComplete()

    // verify saved
    repository.findById(po.k)
      .test()
      .expectNext(po)
      .verifyComplete()
  }

  @Test
  fun `create many`() {
    // do save
    val pos = (1..3).map { randomKeyValuePo() }
    val map = pos.associate { it.k to it.v }
    dao.save(map).test().verifyComplete()

    // verify saved
    databaseClient.execute("select k, v from $TABLE_KV where k in (:keys)")
      .bind("keys", pos.map { it.k })
      .`as`(KeyValuePo::class.java)
      .fetch()
      .all()
      .collectList()
      .test()
      .assertNext { result ->
        assertEquals(pos.size, result.size)
        result.forEach { assertEquals(it.v, map[it.k]) }
      }
      .verifyComplete()
  }

  @Test
  fun `update one`() {
    // prepare data
    val po = repository.save(randomKeyValuePo()).block()!!

    // do update
    val newValue = randomValue()
    dao.save(mapOf(po.k to newValue)).test().verifyComplete()

    // verify updated
    repository.findById(po.k)
      .test()
      .expectNext(po.copy(v = newValue))
      .verifyComplete()
  }

  @Test
  fun `update many`() {
    // prepare data
    val pos = (1..3).map { repository.save(randomKeyValuePo()).block()!! }

    // do update
    val newValue = randomValue()
    dao.save(pos.associate { it.k to newValue }).test().verifyComplete()

    // verify updated
    databaseClient.execute("select k, v from $TABLE_KV where k in (:keys)")
      .bind("keys", pos.map { it.k })
      .`as`(KeyValuePo::class.java)
      .fetch()
      .all()
      .collectList()
      .test()
      .assertNext { result ->
        assertEquals(pos.size, result.size)
        result.forEach { assertEquals(it.v, newValue) }
      }
      .verifyComplete()
  }

  @Test
  fun `create and update`() {
    // prepare data
    val random = randomString()
    val toUpdate = repository.save(KeyValuePo(k = "1-$random", v = "1-$random")).block()!!
    val toCreate = repository.save(KeyValuePo(k = "2-$random", v = "2-$random")).block()!!

    // do create and update
    val map = mapOf(toUpdate.k to toUpdate.v, toCreate.k to toCreate.v)
    dao.save(map).test().verifyComplete()

    // verify
    databaseClient.execute("select k, v from $TABLE_KV where k in (:keys)")
      .bind("keys", listOf(toUpdate.k, toCreate.k))
      .`as`(KeyValuePo::class.java)
      .fetch()
      .all()
      .collectList()
      .test()
      .assertNext { result ->
        assertEquals(2, result.size)
        result.forEach { assertEquals(it.v, map[it.k]) }
      }
      .verifyComplete()
  }
}