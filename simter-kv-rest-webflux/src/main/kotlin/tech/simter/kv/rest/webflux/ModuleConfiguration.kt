package tech.simter.kv.rest.webflux

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router
import tech.simter.kv.PACKAGE
import tech.simter.kv.rest.webflux.handler.DeleteKeyValueHandler
import tech.simter.kv.rest.webflux.handler.FindKeyValueHandler
import tech.simter.kv.rest.webflux.handler.SaveKeyValueHandler
import tech.simter.reactive.web.Utils.TEXT_PLAIN_UTF8

/**
 * All configuration for this module.
 *
 * Register a `RouterFunction<ServerResponse>` with all routers for this module.
 * The default context-path of this router is '/kv'. And can be config by property `simter-kv.rest-context-path`.
 *
 * @author RJ
 */
@Configuration("$PACKAGE.rest.webflux.ModuleConfiguration")
@ComponentScan
class ModuleConfiguration @Autowired constructor(
  @Value("\${simter-kv.rest-context-path:/kv}") private val contextPath: String,
  @Value("\${simter-kv.version:UNKNOWN}") private val version: String,
  private val getValueHandler: FindKeyValueHandler,
  private val saveKeyValueHandler: SaveKeyValueHandler,
  private val deleteKeyValueHandler: DeleteKeyValueHandler
) {
  private val logger = LoggerFactory.getLogger(ModuleConfiguration::class.java)

  init {
    logger.warn("simter-kv.rest-context-path='{}'", contextPath)
  }

  /** Register a `RouterFunction<ServerResponse>` with all routers for this module */
  @Bean("$PACKAGE.rest.webflux.Routes")
  @ConditionalOnMissingBean(name = ["$PACKAGE.rest.webflux.Routes"])
  fun kvRoutes() = router {
    contextPath.nest {
      // GET /{key}
      FindKeyValueHandler.REQUEST_PREDICATE.invoke(getValueHandler::handle)
      // DELETE /{key}
      DeleteKeyValueHandler.REQUEST_PREDICATE.invoke(deleteKeyValueHandler::handle)
      // GET /
      GET("/") { ok().contentType(TEXT_PLAIN_UTF8).bodyValue("simter-kv-$version") }
      // POST /
      SaveKeyValueHandler.REQUEST_PREDICATE.invoke(saveKeyValueHandler::handle)
    }
  }
}