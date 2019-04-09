package tech.simter.kv.dao.jpa

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.test.test
import tech.simter.kv.dao.KeyValueDao
import tech.simter.kv.po.KeyValue
import tech.simter.reactive.jpa.ReactiveEntityManager
import tech.simter.reactive.test.jpa.ReactiveDataJpaTest
import java.util.*

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
    dao.valueOf(UUID.randomUUID().toString())
      .test()
      .expectNextCount(0L)
      .verifyComplete()
  }

  @Test
  fun `key exists`() {
    val po = KeyValue(UUID.randomUUID().toString(), "v")
    rem.persist(po)              // prepare data
      .then(dao.valueOf(po.k)) // invoke
      .test()
      .expectNext(po.v)      // verify
      .verifyComplete()
  }
}