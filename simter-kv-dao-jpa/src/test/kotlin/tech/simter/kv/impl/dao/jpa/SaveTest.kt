package tech.simter.kv.impl.dao.jpa

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.kotlin.test.test
import tech.simter.kv.core.KeyValueDao
import tech.simter.kv.impl.dao.jpa.TestHelper.randomKeyValuePo
import tech.simter.kv.impl.dao.jpa.po.KeyValuePo
import tech.simter.reactive.test.jpa.ReactiveDataJpaTest
import tech.simter.reactive.test.jpa.TestEntityManager
import tech.simter.util.RandomUtils.randomString
import java.util.*

/**
 * @author RJ
 */
@SpringJUnitConfig(UnitTestConfiguration::class)
@ReactiveDataJpaTest
class SaveTest @Autowired constructor(
  val rem: TestEntityManager,
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
    val po = randomKeyValuePo()
    dao.save(mapOf(po.k to po.v)).test().verifyComplete()

    // verify saved
    assertEquals(po, rem.find(KeyValuePo::class.java, po.k).get())
  }

  @Test
  fun `save many`() {
    // do save
    val random = randomString()
    val pos = (1..3).map { KeyValuePo("$random-$it", "$random-$it") }
    dao.save(pos.associate { it.k to it.v }).test().verifyComplete()

    // verify saved
    pos.forEach {
      assertEquals(
        Optional.of(it),
        rem.querySingle { em ->
          em.createQuery("select kv from KeyValuePo kv where kv.k = :key", KeyValuePo::class.java)
            .setParameter("key", it.k)
        }
      )
    }
  }
}