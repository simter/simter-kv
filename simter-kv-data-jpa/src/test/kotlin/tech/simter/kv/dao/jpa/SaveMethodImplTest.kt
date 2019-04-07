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
class SaveMethodImplTest @Autowired constructor(
  val rem: ReactiveEntityManager,
  val dao: KeyValueDao
) {
  @Test
  fun `save nothing`() {
    val none = mapOf<String, String>()
    dao.save(none).test().expectNextCount(0L).verifyComplete()
  }

  @Test
  fun `save one`() {
    // do save
    val po = KeyValue(UUID.randomUUID().toString(), "v")
    dao.save(mapOf(po.key to po.value)).test().verifyComplete()

    // verify saved
    rem.createQuery("select a from KeyValue a where key = :key", KeyValue::class.java)
      .setParameter("key", po.key)
      .singleResult
      .test()
      .expectNext(po)
      .verifyComplete()
  }

  @Test
  fun `save many`() {
    // do save
    val random = UUID.randomUUID().toString();
    val pos = (1..3).map { KeyValue("$random-$it", "$random-$it") }
    dao.save(pos.associate { it.key to it.value }).test().expectNextCount(0L).verifyComplete()

    // verify saved
    pos.forEach {
      rem.createQuery("select a from KeyValue a where key = :key", KeyValue::class.java)
        .setParameter("key", it.key)
        .singleResult
        .test()
        .expectNext(it)
        .verifyComplete()
    }
  }
}