package tech.simter.kv.rest.webflux

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.TEXT_PLAIN
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.router
import tech.simter.kv.rest.webflux.handler.DeleteKeyValueHandler
import tech.simter.kv.rest.webflux.handler.FindKeyValueHandler
import tech.simter.kv.rest.webflux.handler.SaveKeyValueHandler

private const val MODULE_PACKAGE = "tech.simter.kv.rest.webflux"

/**
 * All configuration for this module.
 *
 * Register a `RouterFunction<ServerResponse>` with all routers for this module.
 * The default context-path of this router is '/'. And can be config by property `simter.rest.context-path.kv`.
 *
 * @author RJ
 */
@Configuration("$MODULE_PACKAGE.ModuleConfiguration")
@ComponentScan(MODULE_PACKAGE)
@EnableWebFlux
class ModuleConfiguration @Autowired constructor(
  @Value("\${simter.rest.context-path.kv:/}") private val contextPath: String,
  private val getValueHandler: FindKeyValueHandler,
  private val saveKeyValueHandler: SaveKeyValueHandler,
  private val deleteKeyValueHandler: DeleteKeyValueHandler
) {
  private val logger = LoggerFactory.getLogger(ModuleConfiguration::class.java)

  init {
    logger.warn("simter.rest.context-path.kv='{}'", contextPath)
  }

  /** Register a `RouterFunction<ServerResponse>` with all routers for this module */
  @Bean("tech.simter.kv.rest.webflux.Routes")
  @ConditionalOnMissingBean(name = ["tech.simter.kv.rest.webflux.Routes"])
  fun kvRoutes() = router {
    contextPath.nest {
      // GET /{key}
      FindKeyValueHandler.REQUEST_PREDICATE.invoke(getValueHandler::handle)
      // DELETE /{key}
      DeleteKeyValueHandler.REQUEST_PREDICATE.invoke(deleteKeyValueHandler::handle)
      // GET /
      GET("/", { ok().contentType(TEXT_PLAIN).syncBody("simter-kv module") })
      // POST /
      SaveKeyValueHandler.REQUEST_PREDICATE.invoke(saveKeyValueHandler::handle)
    }
  }
}