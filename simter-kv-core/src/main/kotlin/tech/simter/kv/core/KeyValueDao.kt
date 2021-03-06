package tech.simter.kv.core

import reactor.core.publisher.Mono

/**
 * The key-value pair Dao Interface.
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
   * If the key in [keys] contains a char `%`, use fuzzy search to find all the matched key-value pair, otherwise use equation way.
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