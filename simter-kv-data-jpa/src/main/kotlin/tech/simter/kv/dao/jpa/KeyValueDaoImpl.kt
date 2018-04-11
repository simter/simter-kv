package tech.simter.kv.dao.jpa

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import tech.simter.kv.dao.KeyValueDao
import tech.simter.kv.po.KeyValue


/**
 * The JPA implementation of [KeyValueDao].
 *
 * @author RJ
 */
@Component
class KeyValueDaoImpl @Autowired constructor(private val jpaDao: KeyValueJpaDao) : KeyValueDao {
  override fun findById(id: String): Mono<KeyValue> {
    return Mono.justOrEmpty(jpaDao.findById(id))
  }

  override fun findAllById(ids: Iterable<String>): Flux<KeyValue> {
    return Flux.fromIterable(jpaDao.findAllById(ids))
  }

  override fun save(entity: KeyValue): Mono<KeyValue> {
    return Mono.just(jpaDao.save(entity))
  }

  override fun saveAll(kvs: Flux<KeyValue>): Flux<KeyValue> {
    return Flux.fromIterable(jpaDao.saveAll(kvs.toIterable()))
  }
}