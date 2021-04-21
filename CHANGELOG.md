# simter-kv changelog

## 2.0.0-M2 - 2021-04-21

- Upgrade to simter-3.0.0-M2 (spring-boot-2.4.5)
- Add fuzzy search api
    > If the key contains a char `%`, use fuzzy search to find all the matched key-value pair, otherwise use equation way.

## 2.0.0-M1 - 2020-12-17

- Upgrade to simter-3.0.0-M1 (spring-boot-2.4.1)
- Add `@Transactional` config on `KeyValueServiceImpl`

## 1.0.0 - 2020-11-23

- Upgrade to simter-2.0.0 (spring-boot-2.3.6)
- First stable version.

## 0.13.0 2020-06-05

- Add module simter-kv-dao-web
- Add module simter-kv-service-impl
- Polishing - dao-mongo|jpa|r2dbc unit test code
- Add module simter-kv-test
- Move ImmutableKeyValue to KeyValue.Impl
- Use `@DataR2dbcTest` instead of `@SpringBootTest` on dap-r2dbc module
- Upgrade to simter-2.0.0-M1

## 0.12.0 2020-05-08

- Upgrade to simter-1.3.0-M15

## 0.11.0 2020-04-16

- Upgrade to simter-1.3.0-M14

## 0.10.0 2020-04-09

- Fixed jpa-eclipselink error
- Package starter finalName with platform type as suffix
- Log main config property on starter with info level
- Rename property 'module.rest-context-path.simter-kv' to 'simter-kv.rest-context-path'
- Rename property 'module.authorization.simter-kv' to 'simter-kv.authorization'

## 0.9.0 2020-04-03

- Rename `spring.version` to `spring-framework.version`
- Upgrade to simter-1.3.0-M13
- Change to use stable spring version on dao-r2dbc module
- Remove r2dbc-client implementation [#15](https://github.com/simter/simter-kv/issues/15)
    > Use spring-data-r2dbc to implement KeyValueDao instead of r2dbc-client
- Support r2dbc-mysql [#16](https://github.com/simter/simter-kv/issues/16)
- Support r2dbc-mssql [#17](https://github.com/simter/simter-kv/issues/17)
- Add db.options property for embedded-h2
- Minimize Hikari config

## 0.8.0 2020-01-08

- Upgrade to simter-1.3.0-M11

## 0.7.0 2019-07-04

- Upgrade to simter-1.2.0
- Use stable spring version on main branch
- Use milestone spring version for r2dbc
- Support jpa-eclipselink-x profiles on starter module

## 0.7.0-M5 2019-06-10

- Upgrade to simter-1.2.0-M6
- Refactor module structure to make core api simplify and clear [#12](https://github.com/simter/simter-kv/issues/12)

## 0.7.0-M4 2019-05-10

- Rename simter-kv-dependencies to simter-kv-bom
- Upgrade to simter-1.2.0-M5
- Implements data-r2dbc
- Use TestEntityManager instead ReactiveEntityManager

## 0.7.0-M3 2019-04-12

- Rename TABLE_NAME to TABLE_KV
- Rename PACKAGE_NAME to PACKAGE
- Rename simter-kv-build to simter-kv
- Rename groupId tech.simter to tech.simter.kv [#9](https://github.com/simter/simter-kv/issues/9)
- Support static file on starter [#10](https://github.com/simter/simter-kv/issues/10)

## 0.7.0-M2 2019-04-11

- Rename maven property jpa.ddl-auto to db.jpa-ddl-auto
- Change default database username to tester
- Support EclipseLink for JPA implementation [#7](https://github.com/simter/simter-kv/issues/7)
- Rename KeyValue.Key|Value to KeyValue.k|v to avoid use reserved word
- Add run all data-jpa test script - test.sh
- Set starter default profile to reactive-embedded-mongodb
- Add Rest API document
- Upgrade to simter-1.2.0-M3
- Simplify data-jpa module profiles

## 0.7.0-M1 2019-04-08

- Support use ModuleAuthorizer to verify permission when call service method
- Separate each dao.jpa method test to a standalone test class
- Add h2, hsql, derby and mysql sql
- Support embedded-postgres test on module data-jpa
- Support embedded-mysql test on module data-jpa
- Support embedded-derby test on module data-jpa
- Convert module data-jpa's block dao to reactive by ReactiveJpaWrapper

## 0.6.0 2019-01-14

- Upgrade to simter-build-1.1.0 and simter-dependencies-1.1.0

## 0.5.0 2019-01-10

- Upgrade to simter-build-1.0.0

## 0.4.0 2018-12-03

- Upgrade to simter-build-0.7.0 (with jpa-2.2)

## 0.3.1 2018-08-27

- Rename 'simter.version.kv' to 'module.version.simter-kv'
- Rename 'simter.rest.context-path.kv:/' to 'module.rest-context-path.simter-kv:/kv'

## 0.3.0 2018-08-27

- Remove `@EnableWebFlux` from rest module - it should be only config on starter
- Upgrade to simter-build-0.6.0

## 0.2.0 2018-06-29

- Polishing

## 0.1.0 2018-06-20

- Initial base on simter-build-0.5.0