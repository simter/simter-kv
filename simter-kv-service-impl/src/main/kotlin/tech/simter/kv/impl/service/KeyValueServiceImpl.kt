package tech.simter.kv.impl.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import tech.simter.kv.AUTHORIZER_KEY
import tech.simter.kv.OPERATION_DELETE
import tech.simter.kv.OPERATION_READ
import tech.simter.kv.OPERATION_SAVE
import tech.simter.kv.core.KeyValueDao
import tech.simter.kv.core.KeyValueService
import tech.simter.reactive.security.ModuleAuthorizer

/**
 * The Service implementation of [KeyValueService].
 *
 * @author RJ
 */
@Service
class KeyValueServiceImpl @Autowired constructor(
  @Qualifier("$AUTHORIZER_KEY.authorizer")
  private val moduleAuthorizer: ModuleAuthorizer,
  private val dao: KeyValueDao
) : KeyValueService {
  @Transactional(readOnly = true)
  override fun valueOf(key: String): Mono<String> {
    return moduleAuthorizer.verifyHasPermission(OPERATION_READ)
      .then(Mono.defer { dao.valueOf(key) })
  }

  @Transactional(readOnly = true)
  override fun find(vararg keys: String): Mono<Map<String, String>> {
    return moduleAuthorizer.verifyHasPermission(OPERATION_READ)
      .then(Mono.defer { dao.find(*keys) })
  }

  @Transactional(readOnly = false)
  override fun save(keyValues: Map<String, String>): Mono<Void> {
    return moduleAuthorizer.verifyHasPermission(OPERATION_SAVE)
      .then(Mono.defer { dao.save(keyValues) })
  }

  @Transactional(readOnly = false)
  override fun delete(vararg keys: String): Mono<Void> {
    return moduleAuthorizer.verifyHasPermission(OPERATION_DELETE)
      .then(Mono.defer { dao.delete(*keys) })
  }
}