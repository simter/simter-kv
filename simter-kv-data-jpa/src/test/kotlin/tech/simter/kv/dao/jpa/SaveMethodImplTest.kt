package tech.simter.kv.dao.jpa

import org.assertj.core.api.Assertions.assertThat
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
class SaveMethodImplTest @Autowired constructor(
  @PersistenceContext val em: EntityManager,
  val dao: KeyValueDao
) {
  @Test
  fun `save nothing`() {
    val none = mapOf<String, String>()
    dao.save(none).test().expectNextCount(0L).verifyComplete()
  }

  @Test
  fun `save one`() {
    val po = KeyValue(UUID.randomUUID().toString(), "v")
    dao.save(mapOf(po.key to po.value)).test().expectNextCount(0L).verifyComplete()

    // verify saved
    assertThat(
      em.createQuery("select a from KeyValue a where key = :key", KeyValue::class.java)
        .setParameter("key", po.key).singleResult
    ).isEqualTo(po)
  }

  @Test
  fun `save many`() {
    val pos = (1..3).map { KeyValue("k-$it", "v-$it") }

    // verify result
    dao.save(pos.associate { it.key to it.value }).test().expectNextCount(0L).verifyComplete()

    // verify saved
    pos.forEach {
      assertThat(
        em.createQuery("select a from KeyValue a where key = :key", KeyValue::class.java)
          .setParameter("key", it.key).singleResult
      ).isEqualTo(it)
    }
  }
}