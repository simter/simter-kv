package tech.simter.kv.impl.dao.jpa

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.kotlin.test.test
import tech.simter.kv.core.KeyValueDao
import tech.simter.kv.impl.dao.jpa.po.KeyValuePo
import tech.simter.kv.test.TestHelper.randomKey
import tech.simter.reactive.test.jpa.ReactiveDataJpaTest
import tech.simter.reactive.test.jpa.TestEntityManager
import java.util.*

/**
 * @author RJ
 */
@SpringJUnitConfig(UnitTestConfiguration::class)
@ReactiveDataJpaTest
class DeleteMethodImplTest @Autowired constructor(
  val rem: TestEntityManager,
  val dao: KeyValueDao
) {
  @Test
  fun `delete nothing`() {
    dao.delete().test().verifyComplete()
  }

  @Test
  fun `delete not exists key`() {
    dao.delete(randomKey()).test().verifyComplete()
  }

  @Test
  fun `delete exists key`() {
    // prepare data
    val random = randomKey()
    val pos = (1..3).map { KeyValuePo("$random-$it", "$random-$it") }
    rem.persist(*pos.toTypedArray())

    // do delete
    dao.delete(*pos.map { it.k }.toTypedArray()).test().verifyComplete()

    // verify deleted
    assertEquals(
      Optional.of(0L),
      rem.querySingle { em ->
        em.createQuery("select count(kv) from KeyValuePo kv where kv.k in :keys", Long::class.javaObjectType)
          .setParameter("keys", pos.map { it.k })
      }
    )
  }
}