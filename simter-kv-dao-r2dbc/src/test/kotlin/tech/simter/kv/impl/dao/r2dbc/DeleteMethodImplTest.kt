package tech.simter.kv.impl.dao.r2dbc

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.kotlin.test.test
import tech.simter.kv.TABLE_KV
import tech.simter.kv.core.KeyValueDao
import tech.simter.kv.impl.dao.r2dbc.TestHelper.randomKeyValuePo
import tech.simter.kv.test.TestHelper.randomKey

/**
 * Test [KeyValueDaoImpl.delete].
 *
 * @author RJ
 */
@SpringJUnitConfig(UnitTestConfiguration::class)
@DataR2dbcTest
class DeleteMethodImplTest @Autowired constructor(
  private val databaseClient: DatabaseClient,
  private val repository: KeyValueRepository,
  private val dao: KeyValueDao
) {
  @Test
  fun `delete nothing`() {
    dao.delete().test().verifyComplete()
  }

  @Test
  fun `delete not exists key`() {
    dao.delete(randomKey()).test().verifyComplete()
  }

  @Test
  fun `delete one`() {
    // prepare data
    val po = repository.save(randomKeyValuePo()).block()!!

    // delete it
    dao.delete(po.k).test().verifyComplete()

    // verify deleted
    repository.findById(po.k).test().verifyComplete()
  }

  @Test
  fun `delete many`() {
    // prepare data
    val pos = (1..3).map { repository.save(randomKeyValuePo()).block()!! }

    // do delete
    dao.delete(*pos.map { it.k }.toTypedArray()).test().verifyComplete()

    // verify deleted
    databaseClient.sql("select k from $TABLE_KV where k in (:keys)")
      .bind("keys", pos.map { it.k })
      .fetch()
      .all()
      .test()
      .verifyComplete()
  }
}