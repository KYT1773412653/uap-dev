CREATE TABLE IF NOT EXISTS `uap_msg_info` (
  `id` bigint(20) NOT NULL,
  `msg_title` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `msg_content` varchar(2048) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `msg_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  `tenancy_id` bigint(20) DEFAULT NULL,
  `sender_nm` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `state` varchar(8) CHARACTER SET utf8 COLLATE utf8_bin,
  `msg_level` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `uap_msg_receiver` (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `msg_id` bigint(20) NOT NULL,
  `state` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `update_time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;