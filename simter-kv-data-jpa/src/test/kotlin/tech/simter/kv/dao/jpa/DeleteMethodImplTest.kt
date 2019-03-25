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
@SpringJUnitConfig(ModuleConfiguration::class)
@DataJpaTest
class DeleteMethodImplTest @Autowired constructor(
  @PersistenceContext val em: EntityManager,
  val dao: KeyValueDao
) {
  @Test
  fun delete() {
    // 1. none key
    dao.delete().test().expectNextCount(0L).verifyComplete()

    // 2. delete not exists key
    dao.delete(UUID.randomUUID().toString()).test().expectNextCount(0L).verifyComplete()

    // 3. delete exists key
    // 3.1 prepare data
    val pos = (1..3).map { KeyValue("k-$it", "v-$it") }
    pos.forEach { em.persist(it) }
    em.flush()
    em.clear()

    // 3.2 verify empty result
    dao.delete(*pos.map { it.key }.toTypedArray()).test().expectNextCount(0L).verifyComplete()

    // 3.4 verify deleted
    assertEquals(0L,
      em.createQuery("select count(key) from KeyValue where key in (:keys)")
        .setParameter("keys", pos.map { it.key })
        .singleResult
    )
  }
}