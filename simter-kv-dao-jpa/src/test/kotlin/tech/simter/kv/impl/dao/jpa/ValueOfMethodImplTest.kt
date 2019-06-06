package tech.simter.kv.impl.dao.jpa

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.kotlin.test.test
import tech.simter.kv.core.KeyValueDao
import tech.simter.kv.impl.dao.jpa.TestHelper.randomString
import tech.simter.kv.impl.dao.jpa.po.KeyValuePo
import tech.simter.reactive.test.jpa.ReactiveDataJpaTest
import tech.simter.reactive.test.jpa.TestEntityManager

/**
 * @author RJ
 */
@SpringJUnitConfig(UnitTestConfiguration::class)
@ReactiveDataJpaTest
class ValueOfMethodImplTest @Autowired constructor(
  val rem: TestEntityManager,
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
    val po = KeyValuePo(randomString(), "v")
    rem.persist(po)

    // invoke and verify
    dao.valueOf(po.k)
      .test().expectNext(po.v).verifyComplete()
  }
}