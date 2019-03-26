/**
 * Create table script.
 * @author RJ
 */
create table st_kv (
  key   varchar(100) primary key,
  value text not null
);
comment on table st_kv is 'Key-Value Pair';
comment on column st_kv.key is 'Key';
comment on column st_kv.value is 'Value';