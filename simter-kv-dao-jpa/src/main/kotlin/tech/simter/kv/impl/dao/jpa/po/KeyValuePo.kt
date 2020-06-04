package tech.simter.kv.impl.dao.jpa.po

import tech.simter.kv.TABLE_KV
import tech.simter.kv.core.KeyValue
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * The JPA Entity implementation of [KeyValue].
 *
 * @author RJ
 */
@Entity
@Table(name = TABLE_KV)
data class KeyValuePo(
  @Id
  @Column(length = 100)
  override val k: String,
  override val v: String
) : KeyValue {
  companion object {
    fun from(kv: KeyValue): KeyValuePo {
      return if (kv is KeyValuePo) kv else KeyValuePo(k = kv.k, v = kv.v)
    }
  }
}