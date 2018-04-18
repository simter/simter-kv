package tech.simter.kv.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import tech.simter.kv.dao.KeyValueDao

@Component
@Transactional
class KeyValueServiceImpl @Autowired constructor(
  private val dao: KeyValueDao
) : KeyValueService {
  override fun valueOf(key: String): Mono<String> {
    return dao.valueOf(key)
  }

  override fun find(vararg keys: String): Mono<Map<String, String>> {
    return dao.find(*keys)
  }

  override fun save(keyValues: Map<String, String>): Mono<Void> {
    return dao.save(keyValues)
  }
}