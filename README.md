# Simter Key-Value Modules

## Data Structure

**Domain Object: [KeyValue]={k, v}**

| Property Name | Value Type | Remark          |
|---------------|------------|-----------------|
| k             | String     | Key, maxLen=100 |
| v             | String     | Value           |

[KeyValue]: ./simter-kv-data/src/main/kotlin/tech/simter/kv/po/KeyValue.kt

**Database Table: name=st_kv**

| Column Name | Column Type  | Remark          |
|-------------|--------------|-----------------|
| k           | varchar(100) | Key, maxLen=100 |
| v           | text         | Value           |

> Different database should have different column type, check database script from [here](./simter-kv-data/src/main/resources/tech/simter/kv/sql).

## Maven Modules

| Sn | Name                          | Type | Parent                 | Remark
|----|-------------------------------|------|------------------------|--------
| 1  | [simter-kv]                   | pom  | [simter-build]         | Build these modules and define global properties and pluginManagement
| 2  | simter-kv-bom                 | pom  | simter-kv              | Bom of these modules
| 3  | simter-kv-parent              | pom  | simter-kv              | Define global dependencies and plugins
| 4  | simter-kv-data                | jar  | simter-kv-parent       | Service and Dao Interfaces
| 5  | simter-kv-data-reactive-mongo | jar  | simter-kv-parent       | Dao Implementation By Reactive MongoDB
| 6  | simter-kv-data-jpa            | jar  | simter-kv-parent       | Dao Implementation By JPA
| 7  | simter-kv-rest-webflux        | jar  | simter-kv-parent       | Rest API By WebFlux
| 8  | simter-kv-starter             | jar  | simter-kv-parent       | Microservice Starter

## Requirement

- Maven 3.6+
- Kotlin 1.3+
- Java 8+
- Spring Framework 5.1+
- Spring Boot 2.1+
- Reactor 3.2+

[simter-build]: https://github.com/simter/simter-build/tree/master
[simter-kv]: https://github.com/simter/simter-kv
