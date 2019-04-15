package tech.simter.kv.dao.r2dbc

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import tech.simter.kv.PACKAGE

/**
 * All configuration for this module.
 *
 * @author RJ
 */
@Configuration("$PACKAGE.dao.r2dbc.ModuleConfiguration")
@EnableR2dbcRepositories("$PACKAGE.dao.r2dbc")
@ComponentScan("$PACKAGE.dao.r2dbc")
class ModuleConfiguration