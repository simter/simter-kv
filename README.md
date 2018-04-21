# Simter Key-Value Modules

## Requirement

- Maven 3.5.2+
- Kotlin 1.2.31+
- Java 8+
- Spring Framework 5+
- Spring Boot 2+
- Reactor 3+

## Maven Modules

Sn | Name                            | Parent                        | Remark
---|---------------------------------|-------------------------------|--------
1  | [simter-kv-build]               | [simter-build:0.5.0-SNAPSHOT] | Build modules and define global properties and pluginManagement
2  | [simter-kv-dependencies]        | simter-kv-build        | Define global dependencyManagement
3  | [simter-kv-parent]              | simter-kv-dependencies | All sub modules parent module, Define global dependencies and plugins
4  | [simter-kv-data]                | simter-kv-parent | Define Service and Dao Interfaces
5  | [simter-kv-data-jpa]            | simter-kv-parent | Dao Implementation By JPA
6  | [simter-kv-data-reactive-mongo] | simter-kv-parent | Dao Implementation By Reactive MongoDB
7  | [simter-kv-rest-webflux]        | simter-kv-parent | Rest API By WebFlux
8  | [simter-kv-starter]             | simter-kv-parent | Microservice Starter


Remark : Module 1, 2, 3 all has maven-enforcer-plugin and flatten-maven-plugin config. Other modules must not configure them.


[simter-build:0.5.0-SNAPSHOT]: https://github.com/simter/simter-build/tree/master
[simter-kv-build]: https://github.com/simter/simter-kv
[simter-kv-dependencies]: https://github.com/simter/simter-kv/tree/master/simter-kv-dependencies
[simter-kv-parent]: https://github.com/simter/simter-kv/tree/master/simter-kv-parent
[simter-kv-data]: https://github.com/simter/simter-kv/tree/master/simter-kv-data
[simter-kv-data-jpa]: https://github.com/simter/simter-kv/tree/master/simter-kv-data-jpa
[simter-kv-data-reactive-mongo]: https://github.com/simter/simter-kv/tree/master/simter-kv-data-reactive-mongo
[simter-kv-rest-webflux]: https://github.com/simter/simter-kv/tree/master/simter-kv-rest-webflux
[simter-kv-starter]: https://github.com/simter/simter-kv/tree/master/simter-kv-starter