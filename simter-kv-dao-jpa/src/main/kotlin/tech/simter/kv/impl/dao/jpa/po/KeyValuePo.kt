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
) : KeyValue