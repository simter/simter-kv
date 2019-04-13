# simter-kv-data-r2dbc

The [KeyValueDao] implementation by [R2DBC].

## Unit test

Run below command to test the compatibility with different embedded database:

```
mvn test -P embedded-h2 \
&& mvn test -P embedded-postgres
```

If want to run test on host database, manual run below command:

```
mvn test -P postgres
```

> Could change the host database connection params through below `Maven Properties`.

## Maven Properties

| Property Name | Default Value | Remark                    |
|---------------|---------------|---------------------------|
| db.host       | localhost     | Database host             |
| db.name       | testdb        | Database name             |
| db.username   | tester        | Database connect username |
| db.password   | password      | Database connect password |

Use `-D {property-name}={property-value}` to override default value. Such as:

```bash
mvn test -D db.name=testdb
```

## Maven Profiles:

| Name              | Default | Supported |
|-------------------|---------|-----------|
| embedded-h2       | √       | √         |
| embedded-postgres |         | √         |
| postgres          |         | √         |

The default profile is `embedded-h2`.
Use `-P {profile-name}` to override default. Such as:

```bash
mvn test -P {profile-name}
```

> `embedded-postgres` depends on module [simter-embedded-database-ext].


[R2DBC]: https://r2dbc.io
[KeyValueDao]: https://github.com/simter/simter-kv/blob/master/simter-kv-data/src/main/kotlin/tech/simter/kv/dao/YmdDao.kt
[sql/postgres/schema-create.sql]: https://github.com/simter/simter-kv/blob/master/simter-kv-data/src/main/resources/tech/simter/kv/sql/postgres/schema-create.sql
[R2dbcProperties]: https://github.com/simter/simter-r2dbc-ext/blob/master/src/main/kotlin/tech/simter/r2dbc/R2dbcProperties.kt
[simter-embedded-database-ext]: https://github.com/simter/simter-embedded-database-ext
