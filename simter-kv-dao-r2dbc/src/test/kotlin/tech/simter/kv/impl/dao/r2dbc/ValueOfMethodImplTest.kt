package tech.simter.kv.impl.dao.r2dbc

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.kotlin.test.test
import tech.simter.kv.core.KeyValueDao
import tech.simter.kv.impl.dao.r2dbc.TestHelper.randomString

/**
 * Test [KeyValueDaoImpl.valueOf].
 *
 * @author RJ
 */
@SpringJUnitConfig(UnitTestConfiguration::class)
@DataR2dbcTest
class ValueOfMethodImplTest @Autowired constructor(
  private val repository: KeyValueRepository,
  private val dao: KeyValueDao
) {
  @Test
  fun `key not exists`() {
    dao.valueOf(randomString())
      .test().verifyComplete()
  }

  @Test
  fun `key exists`() {
    // prepare data
    val po = repository.save(TestHelper.randomKeyValue()).block()!!

    // invoke and verify
    dao.valueOf(po.k)
      .test()
      .expectNext(po.v)
      .verifyComplete()
  }
}