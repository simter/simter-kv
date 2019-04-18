package tech.simter.kv.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import tech.simter.kv.OPERATION_DELETE
import tech.simter.kv.OPERATION_READ
import tech.simter.kv.OPERATION_SAVE
import tech.simter.kv.PACKAGE
import tech.simter.kv.dao.KeyValueDao
import tech.simter.reactive.security.ModuleAuthorizer

@Service
class KeyValueServiceImpl @Autowired constructor(
  @Qualifier("$PACKAGE.service.ModuleAuthorizer")
  private val moduleAuthorizer: ModuleAuthorizer,
  private val dao: KeyValueDao
) : KeyValueService {
  override fun valueOf(key: String): Mono<String> {
    return moduleAuthorizer.verifyHasPermission(OPERATION_READ).then(
      dao.valueOf(key)
    )
  }

  override fun find(vararg keys: String): Mono<Map<String, String>> {
    return moduleAuthorizer.verifyHasPermission(OPERATION_READ).then(
      dao.find(*keys)
    )
  }

  override fun save(keyValues: Map<String, String>): Mono<Void> {
    return moduleAuthorizer.verifyHasPermission(OPERATION_SAVE).then(
      dao.save(keyValues)
    )
  }

  override fun delete(vararg keys: String): Mono<Void> {
    return moduleAuthorizer.verifyHasPermission(OPERATION_DELETE).then(
      dao.delete(*keys)
    )
  }
}