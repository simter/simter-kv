package tech.simter.kv.dao.jpa

import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.Repository
import tech.simter.kv.po.KeyValue
import java.util.*

/**
 * The block JPA-DAO Repository. See [CrudRepository], [PagingAndSortingRepository] and [SimpleJpaRepository].
 *
 * @author RJ
 */
interface KeyValueJpaDao : Repository<KeyValue, String> {
  /**
   * Find an KeyValue by its id.
   *
   * See [CrudRepository.findById].
   *
   * @param[id] the id
   * @return the KeyValue with the given id or [Optional.empty] if none found
   */
  fun findById(id: String): Optional<KeyValue>

  /**
   * Find all key-value pairs by its key.
   *
   * See [CrudRepository.findAllById].
   *
   * @param[ids] the ids
   * @return the key-value pairs
   */
  fun findAllById(ids: Iterable<String>): Iterable<KeyValue>

  /**
   * Create or update a KeyValue.
   *
   * See [CrudRepository.save].
   *
   * @param KeyValue the KeyValue
   * @return the saved KeyValue
   */
  fun save(KeyValue: KeyValue): KeyValue

  /**
   * Save or update key-value pairs.
   *
   * If the key-value pair exists, update its value. Otherwise create a new key-value pair.
   *
   * See [CrudRepository.saveAll].
   *
   * @param[kvs] the key-value pairs to save or update
   * @return the saved key-value pairs
   */
  fun saveAll(kvs: Iterable<KeyValue>): Iterable<KeyValue>
}