package tech.simter.kv.po

import org.springframework.data.mongodb.core.mapping.Document
import tech.simter.kv.TABLE_NAME
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

/**
 * The key-value pair PO.
 *
 * @author RJ
 */
@Entity
@Table(name = TABLE_NAME)
@Document(collection = TABLE_NAME)
data class KeyValue(
  @javax.persistence.Id
  @org.springframework.data.annotation.Id
  @Column(length = 100)
  val k: String,
  val v: String
)