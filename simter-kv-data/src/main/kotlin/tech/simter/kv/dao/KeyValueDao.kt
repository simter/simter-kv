package tech.simter.kv.dao

import org.springframework.data.repository.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import tech.simter.kv.po.KeyValue

/**
 * The Key-Value Service Interface.
 *
 * @author RJ
 */
interface KeyValueDao : Repository<KeyValue, String> {
  /**
   *  Retrieves a [KeyValue] by its id.
   *
   *  @param[id] the id for matching.
   *  @return [Mono] emitting the [KeyValue] with the given key or [Mono.empty] if none found.
   */
  fun findById(id: String): Mono<KeyValue>

  /**
   * Find all key-value pairs by its key.
   *
   * @param[ids] the keys
   * @return the key-value pairs
   */
  fun findAllById(ids: Iterable<String>): Flux<KeyValue>

  /**
   * Save a given [KeyValue].
   *
   * @param[entity] the [KeyValue] to save
   * @return [Mono] emitting the saved [KeyValue]
   */
  fun save(entity: KeyValue): Mono<KeyValue>

  /**
   * Save or update key-value pairs.
   *
   * If the key-value pair exists, update its value. Otherwise create a new key-value pair.
   *
   * @param[kvs] the key-value pairs to save or update
   * @return [Flux] emitting the saved [KeyValue] pairs
   */
  fun saveAll(kvs: Flux<KeyValue>): Flux<KeyValue>
}