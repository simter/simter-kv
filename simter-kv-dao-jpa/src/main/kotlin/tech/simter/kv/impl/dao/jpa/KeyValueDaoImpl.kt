package tech.simter.kv.impl.dao.jpa

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import tech.simter.kv.core.KeyValueDao
import tech.simter.reactive.jpa.ReactiveJpaWrapper

/**
 * The JPA implementation of [KeyValueDao].
 *
 * @author RJ
 */
@Component
class KeyValueDaoImpl @Autowired constructor(
  private val blockDao: KeyValueBlockDao,
  private val wrapper: ReactiveJpaWrapper
) : KeyValueDao {
  override fun valueOf(key: String): Mono<String> {
    return wrapper.fromCallable { blockDao.valueOf(key) }
      .flatMap { if (it.isPresent) Mono.just(it.get()) else Mono.empty() }
  }

  override fun find(vararg keys: String): Mono<Map<String, String>> {
    return if (keys.isEmpty()) Mono.empty()
    else wrapper.fromCallable { blockDao.find(*keys) }
      .flatMap { if (it.isEmpty()) Mono.empty() else it.toMono() }
  }

  override fun save(keyValues: Map<String, String>): Mono<Void> {
    return if (keyValues.isEmpty()) Mono.empty()
    else wrapper.fromRunnable { blockDao.save(keyValues) }
  }

  override fun delete(vararg keys: String): Mono<Void> {
    return if (keys.isEmpty()) Mono.empty()
    else wrapper.fromRunnable { blockDao.delete(*keys) }
  }
}