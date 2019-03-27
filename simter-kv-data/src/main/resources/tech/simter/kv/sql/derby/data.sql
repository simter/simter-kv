/**
 * Data initialize script.
 * @author RJ
 */
-- module version
insert into "st_kv" ("key", "value") values ('module-version-simter-kv', '${project.version}');