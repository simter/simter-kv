# simter-kv

Simple Application Key-Value Pair Manager.

## Data Structure

**Domain Object: [KeyValue]={k, v}**

| Property Name | Value Type | Remark          |
|---------------|------------|-----------------|
| k             | String     | Key, maxLen=100 |
| v             | String     | Value           |

[KeyValue]: ./simter-kv-core/src/main/kotlin/tech/simter/kv/core/KeyValue.kt

**Database Table: name=st_kv**

| Column Name | Column Type  | Remark          |
|-------------|--------------|-----------------|
| k           | varchar(100) | Key, maxLen=100 |
| v           | text         | Value           |

> The different database should have different column type, check database script from [here](./simter-kv-core/src/main/resources/tech/simter/kv/sql).

## Maven Modules

| Sn | Name                   | Type | Parent                 | Remark
|----|------------------------|------|------------------------|--------
| 1  | [simter-kv]            | pom  | [simter-build]         | Build these modules and define global properties and pluginManagement
| 2  | simter-kv-bom          | pom  | simter-kv              | Bom
| 3  | simter-kv-parent       | pom  | simter-kv              | Define global dependencies and plugins
| 4  | simter-kv-core         | jar  | simter-kv-parent       | Core API: [KeyValue], [KeyValueDao] and [KeyValueService]
| 5  | simter-kv-test         | jar  | simter-kv-parent       | Common unit test helper method
| 6  | simter-kv-dao-mongo    | jar  | simter-kv-parent       | [KeyValueDao] Implementation By Reactive MongoDB
| 7  | simter-kv-dao-r2dbc    | jar  | simter-kv-parent       | [KeyValueDao] Implementation By R2DBC
| 8  | simter-kv-dao-jpa      | jar  | simter-kv-parent       | [KeyValueDao] Implementation By JPA
| 9  | simter-kv-service-impl | jar  | simter-kv-parent       | Default [KeyValueService] Implementation
| 10 | simter-kv-rest-webflux | jar  | simter-kv-parent       | [Rest API] Implementation By WebFlux
| 11 | simter-kv-starter      | jar  | simter-kv-parent       | Microservice Starter

## Requirement

- Maven 3.6+
- Kotlin 1.3+
- Java 8+
- Spring Framework 5.2+
- Spring Boot 2.3+
- Reactor 3.3+

[simter-build]: https://github.com/simter/simter-build
[simter-kv]: https://github.com/simter/simter-kv
[KeyValue]: https://github.com/simter/simter-kv/blob/master/simter-kv-core/src/main/kotlin/tech/simter/kv/core/KeyValue.kt
[KeyValueDao]: https://github.com/simter/simter-kv/blob/master/simter-kv-core/src/main/kotlin/tech/simter/kv/core/KeyValueDao.kt
[KeyValueService]: https://github.com/simter/simter-kv/blob/master/simter-kv-core/src/main/kotlin/tech/simter/kv/core/KeyValueService.kt
[Rest API]: ./docs/rest-api.md