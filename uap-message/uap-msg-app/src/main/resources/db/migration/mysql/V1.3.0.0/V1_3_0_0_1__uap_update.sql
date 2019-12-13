CREATE TABLE IF NOT EXISTS `uap_msg_topic` (
  `code` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `msg_level` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `send_model` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin,
  `app_id` bigint(20) DEFAULT NULL,
  `state` varchar(8) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  `create_user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `uap_msg_topic_receiver` (
  `id` bigint(20) NOT NULL,
  `tenancy_id` bigint(20) NOT NULL,
  `msg_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `receive_tp` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `receive_id` bigint(20) NOT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  `create_user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `msg_code` (`msg_code`),
  CONSTRAINT `uap_msg_info_ibfk_2` FOREIGN KEY (`msg_code`) REFERENCES `uap_msg_topic` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `uap_msg_info` (
  `id` bigint(20) NOT NULL,
  `msg_title` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `msg_content` varchar(2048) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `msg_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `create_time` bigint(20) DEFAULT NULL,
  `sender_nm` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `state` varchar(8) CHARACTER SET utf8 COLLATE utf8_bin,
  PRIMARY KEY (`id`),
  KEY `msg_code` (`msg_code`),
  CONSTRAINT `uap_msg_info_ibfk_1` FOREIGN KEY (`msg_code`) REFERENCES `uap_msg_topic` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `uap_msg_receiver` (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `msg_id` bigint(20) NOT NULL,
  `state` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `update_time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `msg_id` (`msg_id`),
  CONSTRAINT `uap_msg_receiver_ibfk_1` FOREIGN KEY (`msg_id`) REFERENCES `uap_msg_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
