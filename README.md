# Simter Key-Value Modules

## Requirement

- Maven 3.5.2+
- Java 8+
- Spring Framework 5+
- Spring Boot 2+
- Reactor 3+

## Maven Modules

Name                              | Parent                       
----------------------------------|-------------------------------
[simter-kv-build]                 | [simter-build:0.5.0-SNAPSHOT]
[simter-kv-dependencies]          | simter-kv-build
[simter-kv-parent]                | simter-kv-dependencies
[simter-kv-data]                  | simter-kv-parent
[simter-kv-data-jpa]              | simter-kv-parent
[simter-kv-rest-webflux]          | simter-kv-parent
[simter-kv-starter]               | simter-kv-parent


[交通安全综合服务管理平台]: http://gd.122.gov.cn
[simter-build:0.5.0-SNAPSHOT]: https://github.com/simter/simter-build/tree/master
[simter-kv-build]: https://github.com/simter/simter-kv
[simter-kv-dependencies]: https://github.com/simter/simter-kv/tree/master/simter-kv-dependencies
[simter-kv-parent]: https://github.com/simter/simter-kv/tree/master/simter-kv-parent
[simter-kv-data]: https://github.com/simter/simter-kv/tree/master/simter-kv-data
[simter-kv-data-jpa]: https://github.com/simter/simter-kv/tree/master/simter-kv-data-jpa
[simter-kv-rest-webflux]: https://github.com/simter/simter-kv/tree/master/simter-kv-rest-webflux
[simter-kv-starter]: https://github.com/simter/simter-kv/tree/master/simter-kv-starter