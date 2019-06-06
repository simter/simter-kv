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
class SaveMethodImplTest @Autowired constructor(
  val helper: TestHelper,
  val dao: KeyValueDao
) {
  @Test
  fun `save nothing`() {
    val none = mapOf<String, String>()
    dao.save(none).test().verifyComplete()
  }

  @Test
  fun `create one`() {
    // do save
    val po = randomKeyValue()
    dao.save(mapOf(po.k to po.v)).test().verifyComplete()

    // verify saved
    helper.select("select k, v from $TABLE_KV where k = $1", po.k) { row, _ ->
      ImmutableKeyValue(
        k = row.get("k", String::class.java) as String,
        v = row.get("v", String::class.java) as String
      )
    }.test().expectNext(po).verifyComplete()
  }

  @Test
  fun `create many`() {
    // do save
    val random = randomString()
    val pos = (1..3).map { ImmutableKeyValue("$random-$it", "$random-$it") }
    dao.save(pos.associate { it.k to it.v }).test().verifyComplete()

    // verify saved
    val paramMarkers = pos.mapIndexed { index, _ -> "\$${index + 1}" }.joinToString(", ")
    helper.select(
      "select k, v from $TABLE_KV where k in ($paramMarkers)",
      *pos.map { it.k }.toTypedArray()
    ) { row, _ ->
      ImmutableKeyValue(
        k = row.get("k", String::class.java) as String,
        v = row.get("v", String::class.java) as String
      )
    }.collectList().test().expectNext(pos).verifyComplete()
  }

  @Test
  fun `update one`() {
    // prepare data
    val po = randomKeyValue()
    helper.insert(po)
      .test().verifyComplete()

    // do update
    val newValue = randomString()
    dao.save(mapOf(po.k to newValue)).test().verifyComplete()

    // verify update
    helper.select("select v from $TABLE_KV where k = $1", po.k) { row, _ ->
      row.get("v", String::class.java) as String
    }.test().expectNext(newValue).verifyComplete()
  }

  @Test
  fun `update many`() {
    // prepare data
    val random = randomString()
    val pos = (1..3).map { ImmutableKeyValue("$it-$random", "v-$it") }
    helper.insert(*pos.toTypedArray())
      .test().verifyComplete()

    // do update
    val newValue = randomString()
    dao.save(pos.associate { it.k to newValue }).test().verifyComplete()

    // verify update
    val paramMarkers = pos.mapIndexed { index, _ -> "\$${index + 1}" }.joinToString(", ")
    helper.select(
      "select v from $TABLE_KV where k in ($paramMarkers) order by k asc",
      *pos.map { it.k }.toTypedArray()
    ) { row, _ ->
      row.get("v", String::class.java) as String
    }.collectList().test().expectNext(listOf(newValue, newValue, newValue)).verifyComplete()
  }

  @Test
  fun `create and update`() {
    // prepare data
    val random = randomString()
    val toUpdate = ImmutableKeyValue(k = "1-$random", v = "1-$random")
    helper.insert(toUpdate)
      .test().verifyComplete()
    val toCreate = ImmutableKeyValue(k = "2-$random", v = "2-$random")

    // do create and update
    dao.save(mapOf(toUpdate.k to toUpdate.v, toCreate.k to toCreate.v)).test().verifyComplete()

    // verify
    helper.select(
      "select k, v from $TABLE_KV where k in ($1, $2) order by k asc", toUpdate.k, toCreate.k
    ) { row, _ ->
      ImmutableKeyValue(
        k = row.get("k", String::class.java) as String,
        v = row.get("v", String::class.java) as String
      )
    }.test().expectNext(toUpdate).expectNext(toCreate).verifyComplete()
  }
}