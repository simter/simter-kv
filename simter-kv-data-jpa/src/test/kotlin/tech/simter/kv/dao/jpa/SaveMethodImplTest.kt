package tech.simter.kv.dao.jpa

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.test.test
import tech.simter.kv.dao.KeyValueDao
import tech.simter.kv.dao.jpa.TestHelper.randomKeyValue
import tech.simter.kv.dao.jpa.TestHelper.randomString
import tech.simter.kv.po.KeyValue
import tech.simter.reactive.jpa.ReactiveEntityManager
import tech.simter.reactive.test.jpa.ReactiveDataJpaTest

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
    dao.save(none).test().verifyComplete()
  }

  @Test
  fun `save one`() {
    // do save
    val po = randomKeyValue()
    dao.save(mapOf(po.k to po.v)).test().verifyComplete()

    // verify saved
    rem.createQuery("select kv from KeyValue kv where kv.k = :key", KeyValue::class.java)
      .setParameter("key", po.k)
      .singleResult
      .test()
      .expectNext(po)
      .verifyComplete()
  }

  @Test
  fun `save many`() {
    // do save
    val random = randomString()
    val pos = (1..3).map { KeyValue("$random-$it", "$random-$it") }
    dao.save(pos.associate { it.k to it.v }).test().verifyComplete()

    // verify saved
    pos.forEach {
      rem.createQuery("select kv from KeyValue kv where kv.k = :key", KeyValue::class.java)
        .setParameter("key", it.k)
        .singleResult
        .test()
        .expectNext(it)
        .verifyComplete()
    }
  }
}