package tech.simter.kv.dao.jpa

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.test.test
import tech.simter.kv.dao.KeyValueDao
import tech.simter.kv.po.KeyValue
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 * @author RJ
 */
@SpringJUnitConfig(UnitTestConfiguration::class)
@DataJpaTest
class DeleteMethodImplTest @Autowired constructor(
  @PersistenceContext val em: EntityManager,
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
    val pos = (1..3).map { KeyValue("k-$it", "v-$it") }
    pos.forEach { em.persist(it) }
    em.flush()
    em.clear()

    // verify empty result
    dao.delete(*pos.map { it.key }.toTypedArray()).test().verifyComplete()

    // verify deleted
    assertEquals(0L,
      em.createQuery("select count(key) from KeyValue where key in (:keys)")
        .setParameter("keys", pos.map { it.key })
        .singleResult
    )
  }
}