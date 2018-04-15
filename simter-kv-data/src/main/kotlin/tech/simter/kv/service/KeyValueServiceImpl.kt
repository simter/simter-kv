package tech.simter.kv.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import tech.simter.kv.dao.KeyValueDao
import tech.simter.kv.po.KeyValue

@Component
@Transactional
class KeyValueServiceImpl @Autowired constructor(
  private val dao: KeyValueDao
) : KeyValueService {
  override fun getValue(key: String): Mono<String> {
    return dao.findById(key).map { it.value }
  }

  override fun find(vararg keys: String): Mono<Map<String, String>> {
    return when {
      keys.isEmpty() -> Mono.empty()
      keys.size == 1 -> dao.findById(keys[0]).map { mapOf(it.key to it.value) }
      else -> dao.findAllById(keys.asIterable()).collectMap({ it.key }, { it.value })
    }
  }

  override fun save(keyValues: Map<String, String>): Mono<Void> {
    return when {
      keyValues.isEmpty() -> Mono.empty()
      keyValues.size == 1 -> {
        val kv = keyValues.entries.first()
        return dao.save(KeyValue(kv.key, kv.value)).then()
      }
      else -> dao.saveAll(
        Flux.fromIterable(keyValues.toList().map { KeyValue(it.first, it.second) })
      ).then()
    }
  }
}