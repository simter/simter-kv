/**
 * Create table script.
 * @author RJ
 */
create table st_kv (
  `key`   varchar(100) not null comment 'Key',
  `value` text not null comment 'Value',
  primary key (`key`)
) comment = 'Key-Value Pair';