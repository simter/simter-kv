package tech.simter.kv.dao.jpa

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import tech.simter.kv.dao.KeyValueDao
import tech.simter.kv.po.KeyValue
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext


/**
 * The JPA implementation of [KeyValueDao].
 *
 * @author RJ
 */
@Component
class KeyValueDaoImpl @Autowired constructor(
  @PersistenceContext private val em: EntityManager,
  private val repository: KeyValueJpaRepository
) : KeyValueDao {
  override fun valueOf(key: String): Mono<String> {
    val po = repository.findById(key)
    return if (po.isPresent) Mono.just(po.get().value) else Mono.empty()
  }

  override fun find(vararg keys: String): Mono<Map<String, String>> {
    return if (keys.isEmpty()) Mono.empty<Map<String, String>>()
    else {
      val kvs = em.createQuery("select kv from KeyValue kv where key in (:keys)", KeyValue::class.java)
        .setParameter("keys", keys.toList())
        .resultList
      return if (kvs.isEmpty()) Mono.empty() else Mono.just(kvs.associate { it.key to it.value })
    }
  }

  override fun save(keyValues: Map<String, String>): Mono<Void> {
    repository.saveAll(keyValues.map { KeyValue(it.key, it.value) })
    return Mono.empty()
  }

  }
}