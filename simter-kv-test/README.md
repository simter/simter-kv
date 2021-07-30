# simter-kv-test

## 1. Unit test tools in [TestHelper.kt]

A unit test tools for generate random value:

- `fun randomKey(len: Int = 10): String`
- `fun randomValue(): String`
- `fun randomKeyValue(): KeyValue`

## 2. Integration test

### 2.1. Start server

For test purpose, start the test server:

```shell
$ cd ../simter-kv-starter
$ mvn clean spring-boot:run
```

> Ignore this if test on another server.

### 2.2. Run integration test on server

```shell
$ cd ../simter-kv-test
$ mvn clean test -P integration-test
```

This will run all the integration test on each rest-api define in <[rest-api.md]>.

Want to run the integration test on the real server, just add specific param:

| ParamName  | Remark         | Default value
|------------|----------------|---------------
| server.url | server address | http://127.0.0.1:8085/kv

Such as:

```shell
$ mvn clean test -P integration-test -D server.url=http://127.0.0.1:8085/kv
```


[TestHelper.kt]: https://github.com/simter/simter-kv/blob/master/simter-kv-test/src/main/kotlin/tech/simter/kv/test/TestHelper.kt
[rest-api.md]: https://github.com/simter/simter-kv/blob/master/docs/rest-api.md
