package tech.simter.kv.dao.r2dbc

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.test
import tech.simter.kv.dao.KeyValueDao
import tech.simter.kv.dao.r2dbc.TestHelper.Companion.randomKeyValue
import tech.simter.kv.dao.r2dbc.TestHelper.Companion.randomString

/**
 * @author RJ
 */
@SpringBootTest(classes = [UnitTestConfiguration::class])
class ValueOfMethodImplTest @Autowired constructor(
  val helper: TestHelper,
  val dao: KeyValueDao
) {
  @Test
  fun `key not exists`() {
    dao.valueOf(randomString())
      .test().verifyComplete()
  }

  @Test
  fun `key exists`() {
    // prepare data
    val po = randomKeyValue()
    helper.insert(po)
      .test().verifyComplete()

    // invoke and verify
    dao.valueOf(po.k)
      .test().expectNext(po.v).verifyComplete()
  }
}