package tech.simter.kv.impl.dao.web

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.kotlin.test.test
import tech.simter.kv.core.KeyValueDao
import tech.simter.kv.test.TestHelper.randomKey
import tech.simter.kv.test.TestHelper.randomKeyValue

/**
 * Test [KeyValueDaoImpl.valueOf].
 *
 * @author RJ
 */
@SpringJUnitConfig(UnitTestConfiguration::class)
@WebFluxTest
class ValueOfMethodImplTest @Autowired constructor(
  private val dao: KeyValueDao
) {
  @Test
  fun `key not exists`() {
    dao.valueOf(randomKey()).test().verifyComplete()
  }

  @Test
  fun `key exists`() {
    // prepare data
    val kv = randomKeyValue()
    dao.save(mapOf(kv.k to kv.v)).test().verifyComplete()

    // invoke and verify
    dao.valueOf(kv.k).test().expectNext(kv.v).verifyComplete()
  }
}