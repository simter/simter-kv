package tech.simter.kv.impl.dao.mongo.po

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import tech.simter.kv.TABLE_KV
import tech.simter.kv.core.KeyValue

/**
 * The Document Entity implementation of [KeyValue].
 *
 * @author RJ
 */
@Document(collection = TABLE_KV)
data class KeyValuePo(
  @Id
  override val k: String,
  override val v: String
) : KeyValue {
  companion object {
    fun from(kv: KeyValue): KeyValuePo {
      return if (kv is KeyValuePo) kv else KeyValuePo(k = kv.k, v = kv.v)
    }
  }
}