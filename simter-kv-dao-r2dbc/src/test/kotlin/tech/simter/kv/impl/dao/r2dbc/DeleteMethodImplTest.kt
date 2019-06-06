package tech.simter.kv.impl.dao.r2dbc

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.kotlin.test.test
import tech.simter.kv.TABLE_KV
import tech.simter.kv.core.KeyValueDao
import tech.simter.kv.impl.ImmutableKeyValue
import tech.simter.kv.impl.dao.r2dbc.TestHelper.Companion.randomKeyValue
import tech.simter.kv.impl.dao.r2dbc.TestHelper.Companion.randomString

/**
 * @author RJ
 */
@SpringBootTest(classes = [UnitTestConfiguration::class])
class DeleteMethodImplTest @Autowired constructor(
  val helper: TestHelper,
  val dao: KeyValueDao
) {
  @Test
  fun `delete nothing`() {
    dao.delete().test().verifyComplete()
  }

  @Test
  fun `delete not exists key`() {
    dao.delete(randomString()).test().verifyComplete()
  }

  @Test
  fun `delete one`() {
    // prepare data
    val po = randomKeyValue()
    helper.insert(po)
      .test().verifyComplete()

    // invoke and verify
    dao.delete(po.k)
      .test().verifyComplete()

    // verify deleted
    helper.select("select v from $TABLE_KV where k = $1", po.k) { row, _ -> row.get("v", String::class.java) }
      .test().verifyComplete()
  }

  @Test
  fun `delete many`() {
    // prepare data
    val random = randomString()
    val pos = (1..3).map { ImmutableKeyValue("$random-$it", "$random-$it") }
    helper.insert(*pos.toTypedArray()).test().verifyComplete()

    // do delete
    dao.delete(*pos.map { it.k }.toTypedArray()).test().verifyComplete()

    // verify deleted
    val paramMarkers = pos.mapIndexed { index, _ -> "\$${index + 1}" }.joinToString(", ")
    helper.select("select count(*) c from $TABLE_KV where k in ($paramMarkers)", *pos.map { it.k }.toTypedArray()) { row, _ -> row.get("c", Long::class.javaObjectType) }
      .test().expectNext(0L).verifyComplete()
  }
}