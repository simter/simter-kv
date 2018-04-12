#  Simter Key-Value Server

## Requirement

- Maven 3.5.2+
- Java 8+
- Spring Framework 5+
- Spring Boot 2+
- Reactor 3+

## Maven Profiles

Environment | Profile | Persistence  | Remark
------------|---------|--------------|--------
Development |dev      | [HyperSQL]   | JPA
Production  |prod     | [PostgreSQL] | JPA

The default profile is `dev`. Run bellow command to start:

```bash
mvn spring-boot:run -P {profile-name}
```

Default server-port is 8085, use `-D port=8080` to change to another port.

## Maven Properties

Property Name | Default Value | Remark
--------------|---------------|--------
port          | 8085          | Web server port
db.host       | localhost     | Database host
db.name       | kv            | Database name
db.username   | kv            | Database connect username
db.password   | password      | Database connect password
db.init-mode  | never         | Init database by `spring.datasource.schema/data` config. `never` or `always`

Use `-D {property-name}={property-value}` to override default value. Such as:

```bash
mvn spring-boot:run -D port=8081
```

## Build Production

```bash
mvn clean package -P prod
```

## Run Production

```bash
java -jar {package-name}.jar

# or
nohup java -jar {package-name}.jar > /dev/null &
```

[HyperSQL]: http://hsqldb.org
[PostgreSQL]: https://www.postgresql.org