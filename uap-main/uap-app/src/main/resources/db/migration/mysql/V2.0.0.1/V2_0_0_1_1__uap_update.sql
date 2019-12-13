CREATE TABLE `uap_databit` (
  `id` bigint(20) NOT NULL,
  `databit` bigint(20) NOT NULL,
  `description` varchar(256) COLLATE utf8_bin DEFAULT NULL,
  `tenant_id` bigint(20) NOT NULL,
  `reserved` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

alter table uap_user add column databit63 bigint(20);
alter table uap_user add column databit127 bigint(20);






