# Rest API for simter-kv

The following `{context-path}` could be configured by property `module.rest-context-path.simter-kv`. Its default value is `/kv`.

Provide rest APIs:

1. Find key-value pairs by key.
2. Create or update key-value pairs.
3. Delete key-value pairs by key.

## 1. Find key-value pairs by key

Find single key-value pair or multiple key-value pairs. Multiple keys combine with comma. The key could not contains comma symbol `','`.

### Request

```
GET {context-path}/{key}
```

Example: 

```
GET /account           - one key
GET /account,password  - multiple keys combine with comma
```

### Response (if at lease one key exists)

```
200 OK
Content-Type: application/json;charset=UTF-8

{key1: value1, ...}
```

### Response: (if all key not exists)

```
204 No Content
```

## 2. Create or update key-value pairs

If the key is exists update its value, otherwise create a new key-value pair.

### Request

```
POST {context-path}/
Content-Type : application/json;charset=utf-8

{key1: value1, ...}
```

### Response

```
204 No Content
```

## 3. Delete key-value pairs by key

Delete single key-value pair or multiple key-value pairs. Multiple keys combine with comma. The key could not contains comma symbol `','`.

### Request

```
DELETE {context-path}/{key}
```

Example: 

```
DELETE /account           - one key
DELETE /account,password  - multiple keys combine with comma
```

### Response

```
204 No Content
```