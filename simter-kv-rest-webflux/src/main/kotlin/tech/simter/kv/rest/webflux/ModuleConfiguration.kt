package tech.simter.kv.rest.webflux

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.TEXT_PLAIN
import org.springframework.web.reactive.function.server.router
import tech.simter.kv.PACKAGE
import tech.simter.kv.rest.webflux.handler.DeleteKeyValueHandler
import tech.simter.kv.rest.webflux.handler.FindKeyValueHandler
import tech.simter.kv.rest.webflux.handler.SaveKeyValueHandler

/**
 * All configuration for this module.
 *
 * Register a `RouterFunction<ServerResponse>` with all routers for this module.
 * The default context-path of this router is '/kv'. And can be config by property `module.rest-context-path.simter-kv`.
 *
 * @author RJ
 */
@Configuration("$PACKAGE.rest.webflux.ModuleConfiguration")
@ComponentScan("$PACKAGE.rest.webflux")
class ModuleConfiguration @Autowired constructor(
  @Value("\${module.rest-context-path.simter-kv:/kv}") private val contextPath: String,
  @Value("\${module.version.simter-kv:UNKNOWN}") private val version: String,
  private val getValueHandler: FindKeyValueHandler,
  private val saveKeyValueHandler: SaveKeyValueHandler,
  private val deleteKeyValueHandler: DeleteKeyValueHandler
) {
  private val logger = LoggerFactory.getLogger(ModuleConfiguration::class.java)

  init {
    logger.warn("module.version.simter-kv='{}'", version)
    logger.warn("module.rest-context-path.simter-kv='{}'", contextPath)
  }

  /** Register a `RouterFunction<ServerResponse>` with all routers for this module */
  @Bean("$PACKAGE.Routes")
  @ConditionalOnMissingBean(name = ["$PACKAGE.Routes"])
  fun kvRoutes() = router {
    contextPath.nest {
      // GET /{key}
      FindKeyValueHandler.REQUEST_PREDICATE.invoke(getValueHandler::handle)
      // DELETE /{key}
      DeleteKeyValueHandler.REQUEST_PREDICATE.invoke(deleteKeyValueHandler::handle)
      // GET /
      GET("/") { ok().contentType(TEXT_PLAIN).syncBody("simter-kv-$version") }
      // POST /
      SaveKeyValueHandler.REQUEST_PREDICATE.invoke(saveKeyValueHandler::handle)
    }
  }
}