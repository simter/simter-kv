package tech.simter.kv.impl.dao.r2dbc.po

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import tech.simter.kv.TABLE_KV
import tech.simter.kv.core.KeyValue

@Table(TABLE_KV)
data class KeyValuePo(
  @Id
  override val k: String,
  override val v: String
) : KeyValue, Persistable<String> {
  override fun getId(): String {
    return this.k
  }

  override fun isNew(): Boolean {
    return true
  }
}