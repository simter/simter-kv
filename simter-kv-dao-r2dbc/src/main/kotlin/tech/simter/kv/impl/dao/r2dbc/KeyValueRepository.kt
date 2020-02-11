package tech.simter.kv.impl.dao.r2dbc

import org.springframework.data.r2dbc.repository.R2dbcRepository
import tech.simter.kv.impl.dao.r2dbc.po.KeyValuePo

/**
 * The reactive repository.
 *
 * @author RJ
 */
interface KeyValueRepository : R2dbcRepository<KeyValuePo, String>