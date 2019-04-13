# Simter Key-Value Modules

## Requirement

- Maven 3.6+
- Kotlin 1.3+
- Java 8+
- Spring Framework 5.1+
- Spring Boot 2.1+
- Reactor 3.2+

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


[simter-build]: https://github.com/simter/simter-build/tree/master
[simter-kv]: https://github.com/simter/simter-kv
