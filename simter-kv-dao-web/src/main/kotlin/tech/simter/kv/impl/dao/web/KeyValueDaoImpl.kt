package tech.simter.kv.impl.dao.web

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import tech.simter.kv.core.KeyValueDao
import tech.simter.reactive.context.SystemContext
import tech.simter.reactive.web.webfilter.JwtWebFilter.Companion.JWT_HEADER_NAME
import java.util.*
import javax.json.Json
import javax.json.JsonObjectBuilder

/**
 * The implementation of [KeyValueDao] by WebFlux.
 *
 * @author RJ
 */
@Repository
class KeyValueDaoImpl(
  @Qualifier(WEB_CLIENT_KEY)
  val webClient: WebClient
) : KeyValueDao {
  // get `Authorization` header value from context
  private fun getAuthorizationHeader() = SystemContext.getOptional<String>(JWT_HEADER_NAME)

  // set `Authorization` header value to request
  private fun addAuthorizationHeader(request: WebClient.RequestHeadersSpec<*>, auth: Optional<String>) =
    auth.ifPresent { request.header(JWT_HEADER_NAME, auth.get()) }

  override fun valueOf(key: String): Mono<String> {
    return getAuthorizationHeader().flatMap {
      webClient.get().uri("/$key")
        .apply { addAuthorizationHeader(this, it) }
        .retrieve()
        .bodyToMono(Map::class.java)
        .map { it[key] as String }
    }
  }

  @Suppress("UNCHECKED_CAST")
  override fun find(vararg keys: String): Mono<Map<String, String>> {
    return getAuthorizationHeader().flatMap {
      if (keys.isEmpty()) Mono.empty()
      else webClient.get().uri("/${keys.joinToString(",")}")
        .apply { addAuthorizationHeader(this, it) }
        .retrieve()
        .bodyToMono(Map::class.java)
        .map { it as Map<String, String> }
    }
  }

  override fun save(keyValues: Map<String, String>): Mono<Void> {
    return getAuthorizationHeader().flatMap {
      if (keyValues.isEmpty()) Mono.empty()
      else webClient.post()
        .apply { addAuthorizationHeader(this, it) }
        .contentType(APPLICATION_JSON)
        .bodyValue(toJson(keyValues).build().toString())
        .retrieve()
        .bodyToMono(Void::class.java)
    }
  }

  override fun delete(vararg keys: String): Mono<Void> {
    return getAuthorizationHeader().flatMap {
      if (keys.isEmpty()) Mono.empty()
      else webClient.delete().uri("/${keys.joinToString(",")}")
        .apply { addAuthorizationHeader(this, it) }
        .exchange()
        .then()
    }
  }

  private fun toJson(keyValues: Map<String, String>): JsonObjectBuilder {
    val builder = Json.createObjectBuilder()
    keyValues.forEach { (k, v) -> builder.add(k, v) }
    return builder
  }
}