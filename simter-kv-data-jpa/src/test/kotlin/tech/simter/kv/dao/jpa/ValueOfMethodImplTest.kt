package tech.simter.kv.dao.jpa

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
class ValueOfMethodImplTest @Autowired constructor(
  @PersistenceContext val em: EntityManager,
  val dao: KeyValueDao
) {
  @Test
  fun `key not exists`() {
    // verify not exists
    dao.valueOf(UUID.randomUUID().toString())
      .test()
      .expectNextCount(0L)
      .verifyComplete()
  }

  @Test
  fun `key exists`() {
    // prepare data
    val po = KeyValue(UUID.randomUUID().toString(), "v")
    em.persist(po)
    em.flush()
    em.clear()

    // verify exists
    dao.valueOf(po.key)
      .test()
      .expectNext(po.value)
      .verifyComplete()
  }
}