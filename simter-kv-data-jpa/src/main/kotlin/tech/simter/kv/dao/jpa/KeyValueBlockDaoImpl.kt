package tech.simter.kv.dao.jpa

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import tech.simter.kv.po.KeyValue
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.NoResultException
import javax.persistence.PersistenceContext

/**
 * The JPA implementation of [KeyValueBlockDao].
 *
 * @author RJ
 */
@Component
internal class KeyValueBlockDaoImpl @Autowired constructor(
  @PersistenceContext private val em: EntityManager,
  private val repository: KeyValueJpaRepository
) : KeyValueBlockDao {
  @Transactional(readOnly = true)
  override fun valueOf(key: String): Optional<String> {
    return try {
      Optional.of(
        em.createQuery("select v from KeyValue where k = :key", String::class.java)
          .setParameter("key", key)
          .singleResult
      )
    } catch (e: NoResultException) {
      Optional.empty()
    }
  }

  @Transactional(readOnly = true)
  override fun find(vararg keys: String): Map<String, String> {
    return if (keys.isEmpty()) emptyMap()
    else {
      return em.createQuery("select kv from KeyValue kv where k in (:keys)", KeyValue::class.java)
        .setParameter("keys", keys.toList())
        .resultList
        .associate { it.k to it.v }
    }
  }

  @Transactional(readOnly = false)
  override fun save(keyValues: Map<String, String>) {
    repository.saveAll(keyValues.map { KeyValue(it.key, it.value) })
  }

  @Transactional(readOnly = false)
  override fun delete(vararg keys: String) {
    if (keys.isNotEmpty()) {
      em.createQuery("delete from KeyValue where k in (:keys)")
        .setParameter("keys", keys.toList())
        .executeUpdate()
    }
  }
}