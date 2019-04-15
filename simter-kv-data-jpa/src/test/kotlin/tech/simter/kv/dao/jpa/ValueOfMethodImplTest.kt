package tech.simter.kv.dao.jpa

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.test.test
import tech.simter.kv.dao.KeyValueDao
import tech.simter.kv.dao.jpa.TestHelper.randomString
import tech.simter.kv.po.KeyValue
import tech.simter.reactive.jpa.ReactiveEntityManager
import tech.simter.reactive.test.jpa.ReactiveDataJpaTest

/**
 * @author RJ
 */
@SpringJUnitConfig(UnitTestConfiguration::class)
@ReactiveDataJpaTest
class ValueOfMethodImplTest @Autowired constructor(
  val rem: ReactiveEntityManager,
  val dao: KeyValueDao
) {
  @Test
  fun `key not exists`() {
    dao.valueOf(randomString())
      .test()
      .expectNextCount(0L)
      .verifyComplete()
  }

  @Test
  fun `key exists`() {
    // prepare data
    val po = KeyValue(randomString(), "v")
    rem.persist(po).test().verifyComplete()

    // invoke and verify
    dao.valueOf(po.k)
      .test().expectNext(po.v).verifyComplete()
  }
}