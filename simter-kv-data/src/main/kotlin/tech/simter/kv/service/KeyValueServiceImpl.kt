package tech.simter.kv.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import tech.simter.kv.Operation.DELETE
import tech.simter.kv.Operation.READ
import tech.simter.kv.Operation.SAVE
import tech.simter.kv.dao.KeyValueDao
import tech.simter.reactive.security.ModuleAuthorizer

@Component
@Transactional
class KeyValueServiceImpl @Autowired constructor(
  @Qualifier("$MODULE_PACKAGE.ModuleAuthorizer")
  private val moduleAuthorizer: ModuleAuthorizer,
  private val dao: KeyValueDao
) : KeyValueService {
  override fun valueOf(key: String): Mono<String> {
    return moduleAuthorizer.verifyHasPermission(READ).then(dao.valueOf(key))
  }

  override fun find(vararg keys: String): Mono<Map<String, String>> {
    return moduleAuthorizer.verifyHasPermission(READ).then(dao.find(*keys))
  }

  override fun save(keyValues: Map<String, String>): Mono<Void> {
    return moduleAuthorizer.verifyHasPermission(SAVE).then(dao.save(keyValues))
  }

  override fun delete(vararg keys: String): Mono<Void> {
    return moduleAuthorizer.verifyHasPermission(DELETE).then(dao.delete(*keys))
  }
}