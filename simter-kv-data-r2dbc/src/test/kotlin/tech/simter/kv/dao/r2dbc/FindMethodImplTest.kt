package tech.simter.kv.dao.r2dbc

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.test
import tech.simter.kv.dao.KeyValueDao
import tech.simter.kv.dao.r2dbc.TestHelper.Companion.randomString
import tech.simter.kv.po.KeyValue

/**
 * @author RJ
 */
@SpringBootTest(classes = [UnitTestConfiguration::class])
class FindMethodImplTest @Autowired constructor(
  val helper: TestHelper,
  val dao: KeyValueDao
) {
  @Test
  fun `find nothing`() {
    dao.find().test().verifyComplete()
  }

  @Test
  fun `find not exists key`() {
    dao.find(randomString()).test().verifyComplete()
  }

  @Test
  fun `find exists key`() {
    // prepare data
    val random = randomString()
    val pos = (1..3).map { KeyValue("$random-$it", "$random-$it") }
    helper.insert(*pos.toTypedArray())
      .test().verifyComplete()

    // invoke and verify
    dao.find(*pos.map { it.k }.toTypedArray())
      .test()
      .consumeNextWith { actualMap ->
        assertEquals(pos.size, actualMap.size)
        pos.forEach { assertEquals(it.v, actualMap[it.k]) }
      }.verifyComplete()
  }
}