/**
 * Create table script.
 * @author RJ
 */
create table st_kv (
  k varchar(100) not null comment 'Key',
  v text not null comment 'Value',
  primary key (k)
) comment = 'Key-Value Pair';