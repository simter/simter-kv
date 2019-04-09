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
class DeleteMethodImplTest @Autowired constructor(
  val rem: ReactiveEntityManager,
  val dao: KeyValueDao
) {
  @Test
  fun `delete nothing`() {
    dao.delete().test().verifyComplete()
  }

  @Test
  fun `delete not exists key`() {
    dao.delete(UUID.randomUUID().toString()).test().verifyComplete()
  }

  @Test
  fun `delete exists key`() {
    // prepare data
    val random = UUID.randomUUID().toString();
    val pos = (1..3).map { KeyValue("$random-$it", "$random-$it") }
    rem.persist(*pos.toTypedArray()).test().verifyComplete()

    // do delete
    dao.delete(*pos.map { it.k }.toTypedArray()).test().verifyComplete()

    // verify deleted
    rem.createQuery("select count(k) from KeyValue where k in (:keys)", Long::class.javaObjectType)
      .setParameter("keys", pos.map { it.k })
      .singleResult
      .test()
      .expectNext(0L)
      .verifyComplete()
  }
}