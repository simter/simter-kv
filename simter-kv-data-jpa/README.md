# simter-kv-data-jpa

## Unit test

Run below command to test the compatibility with different JPA implementation on different embedded database:

```
./test.sh
```

Or manual run bellow command:

```
mvn test -P jpa-hibernate,embedded-h2 \
&& mvn test -P jpa-hibernate,embedded-hsql \
&& mvn test -P jpa-hibernate,embedded-derby \
&& mvn test -P jpa-hibernate,embedded-postgres \
&& mvn test -P jpa-hibernate,embedded-mysql \
&& mvn test -P jpa-eclipselink,embedded-h2 \
&& mvn test -P jpa-eclipselink,embedded-hsql \
&& mvn test -P jpa-eclipselink,embedded-derby \
&& mvn test -P jpa-eclipselink,embedded-postgres \
&& mvn test -P jpa-eclipselink,embedded-mysql
```

If want to run test on different host database, manual run bellow command:

```
mvn test -P jpa-hibernate,postgres \
&& mvn test -P jpa-eclipselink,postgres \
&& mvn test -P jpa-hibernate,mysql \
&& mvn test -P jpa-eclipselink,mysql
```

> Could change the host database connection params through below `Maven Properties`.

## Maven Properties

| SN | Property Name | Default Value | Remark                    |
|----|---------------|---------------|---------------------------|
|  1 | db.host       | localhost     | Database host             |
|  2 | db.name       | testdb        | Database name             |
|  3 | db.username   | tester        | Database connect username |
|  4 | db.password   | password      | Database connect password |

Use `-D {property-name}={property-value}` to override default value. Such as:

```bash
mvn test -D db.name=testdb
```

## Maven Profiles:

| SN | Name              | Type               | Default |
|----|-------------------|--------------------|---------|
|  1 | jpa-hibernate     | JPA Implementation | true    |
|  2 | jpa-eclipselink   | JPA Implementation |         |
|  3 | embedded-h2       | Embedded Database  | true    |
|  4 | embedded-hsql     | Embedded Database  |         |
|  5 | embedded-derby    | Embedded Database  |         |
|  6 | embedded-postgres | Embedded Database  |         |
|  7 | embedded-mysql    | Embedded Database  |         |
|  8 | postgres          | Host Database      |         |
|  9 | mysql             | Host Database      |         |

The default profile is `jpa-hibernate` and `embedded-h2`.
Use `-P {profile-name}` to override default. Such as:

```bash
mvn test -P {profile-name}
```

## YML Files:

| SN | Name                            | Remark             |
|----|---------------------------------|--------------------|
|  1 | application.yml                 |                    |
|  2 | application-database.yml        | Database Config    |
|  3 | application-jpa-hibernate.yml   | Hibernate Config   |
|  4 | application-jpa-eclipselink.yml | EclipseLink Config |