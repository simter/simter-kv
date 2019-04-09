/**
 * Create table script.
 * @author RJ
 */
create table st_kv (
  k varchar(100) primary key,
  v longvarchar  not null
);
comment on table st_kv is 'Key-Value Pair';
comment on column st_kv.k is 'Key';
comment on column st_kv.v is 'Value';