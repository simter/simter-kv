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

  override fun findAll(vararg keys: String): Flux<KeyValue> {
    return dao.findAllById(keys.asIterable())
  }

  override fun save(kv: KeyValue): Mono<Void> {
    return dao.save(kv).then()
  }

  override fun saveAll(kvs: Flux<KeyValue>): Mono<Void> {
    return dao.saveAll(kvs).then()
  }
}