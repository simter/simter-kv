package tech.simter.kv.impl.dao.mongo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import tech.simter.kv.core.KeyValueDao
import tech.simter.kv.impl.dao.mongo.po.KeyValuePo

/**
 * The Reactive MongoDB implementation of [KeyValueDao].
 *
 * @author RJ
 */
@Component
class KeyValueDaoImpl @Autowired constructor(
  private val repository: KeyValueReactiveRepository
) : KeyValueDao {
  override fun valueOf(key: String): Mono<String> {
    return repository.findById(key).map { it.v }
  }

  override fun find(vararg keys: String): Mono<Map<String, String>> {
    return if (keys.isEmpty()) Mono.empty()
    else repository.findAllById(keys.asIterable())
      .collectMap({ it.k }, { it.v })
      .flatMap { if (it.isEmpty()) Mono.empty() else Mono.just(it) } // switch empty map to Mono.empty()
  }

  override fun save(keyValues: Map<String, String>): Mono<Void> {
    return if (keyValues.isEmpty()) Mono.empty()
    else repository.saveAll(keyValues.map { KeyValuePo(it.key, it.value) }).then()
  }

  override fun delete(vararg keys: String): Mono<Void> {
    return if (keys.isEmpty()) Mono.empty()
    else repository.deleteAll(repository.findAllById(keys.asIterable()))
  }
}