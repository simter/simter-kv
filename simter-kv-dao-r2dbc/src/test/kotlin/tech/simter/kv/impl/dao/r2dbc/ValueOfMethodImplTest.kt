package tech.simter.kv.impl.dao.r2dbc

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.kotlin.test.test
import tech.simter.kv.core.KeyValueDao
import tech.simter.kv.impl.dao.r2dbc.TestHelper.randomKeyValuePo
import tech.simter.kv.test.TestHelper.randomKey

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
    dao.valueOf(randomKey()).test().verifyComplete()
  }

  @Test
  fun `key exists`() {
    // prepare data
    val po = repository.save(randomKeyValuePo()).block()!!

    // invoke and verify
    dao.valueOf(po.k).test().expectNext(po.v).verifyComplete()
  }
}