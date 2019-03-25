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
class FindMethodImplTest @Autowired constructor(
  @PersistenceContext val em: EntityManager,
  val dao: KeyValueDao
) {
  @Test
  fun `find nothing`() {
    dao.find().test().expectNextCount(0L).verifyComplete()
  }

  @Test
  fun `find not exists key`() {
    dao.find(UUID.randomUUID().toString()).test().expectNextCount(0L).verifyComplete()
  }

  @Test
  fun `find exists key`() {
    // prepare data
    val pos = (1..3).map { KeyValue("k-$it", "v-$it") }
    pos.forEach { em.persist(it) }
    em.flush()
    em.clear()

    // invoke and verify
    dao.find(*pos.map { it.key }.toTypedArray())
      .test()
      .consumeNextWith { actualMap ->
        assertEquals(pos.size, actualMap.size)
        pos.forEach { assertEquals(it.value, actualMap[it.key]) }
      }.verifyComplete()
  }
}