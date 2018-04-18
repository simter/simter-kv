package tech.simter.kv.dao

import reactor.core.publisher.Mono
import tech.simter.kv.service.KeyValueService

/**
 * The Key-Value Dao Interface.
 *
 * This interface should only be use by [KeyValueService]. It is design to public just for multiple Dao implements.
 *
 * @author RJ
 */
interface KeyValueDao {
  /**
   * Retrieves a value by key.
   *
   * @param[key] the key
   * @return [Mono] emitting the the value if key exists or [Mono.empty] otherwise.
   */
  fun valueOf(key: String): Mono<String>

  /**
   * Find all key-value pairs by keys.
   *
   * @param[keys] the keys
   * @return [Mono] emitting key-value pairs store in a map if at lease one key exists or [Mono.empty] otherwise.
   */
  fun find(vararg keys: String): Mono<Map<String, String>>

  /**
   * Create or update key-value pairs.
   *
   * Update the value if key exists or create a new key-value pair otherwise .
   *
   * @param[keyValues] the key-value pairs to save or update
   * @return [Mono] signaling when operation has completed
   */
  fun save(keyValues: Map<String, String>): Mono<Void>

  /**
   * Delete key-value pairs by key.
   *
   * @param[keys] the keys to delete
   * @return [Mono] signaling when operation has completed
   */
  fun delete(vararg keys: String): Mono<Void>
}