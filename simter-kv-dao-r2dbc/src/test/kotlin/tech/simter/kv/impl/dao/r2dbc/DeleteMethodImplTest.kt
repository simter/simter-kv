package tech.simter.kv.impl.dao.r2dbc

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.kotlin.test.test
import tech.simter.kv.TABLE_KV
import tech.simter.kv.core.KeyValueDao
import tech.simter.kv.impl.dao.r2dbc.TestHelper.randomKeyValue
import tech.simter.kv.impl.dao.r2dbc.TestHelper.randomString

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
    // delete nothing
    dao.delete().test().verifyComplete()
  }

  @Test
  fun `delete not exists key`() {
    dao.delete(randomString()).test().verifyComplete()
  }

  @Test
  fun `delete one`() {
    // prepare data
    val po = repository.save(randomKeyValue()).block()!!

    // delete it
    dao.delete(po.k).test().verifyComplete()

    // verify deleted
    repository.findById(po.k).test().verifyComplete()
  }

  @Test
  fun `delete many`() {
    // prepare data
    val pos = (1..3).map { repository.save(randomKeyValue()).block()!! }

    // do delete
    dao.delete(*pos.map { it.k }.toTypedArray()).test().verifyComplete()

    // verify deleted
    databaseClient.execute("select k from $TABLE_KV where k in (:keys)")
      .bind("keys", pos.map { it.k })
      .fetch()
      .all()
      .test()
      .verifyComplete()
  }
}