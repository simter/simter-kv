package tech.simter.kv.po

import org.springframework.data.mongodb.core.mapping.Document
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

/**
 * The key-value pair PO.
 *
 * @author RJ
 */
@Entity
@Table(name = "st_kv")
@Document(collection="st_kv")
data class KeyValue(
  @javax.persistence.Id
  @org.springframework.data.annotation.Id
  @Column(length = 100)
  val key: String,
  val value: String
)