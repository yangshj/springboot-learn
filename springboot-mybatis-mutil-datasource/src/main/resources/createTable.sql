CREATE TABLE `city` (
  `id` bigint(20) NOT NULL DEFAULT '0',
  `province_id` bigint(20) DEFAULT NULL COMMENT '省份编号',
  `city_name` varchar(255) DEFAULT NULL COMMENT '城市名称',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user` (
  `id` bigint(20) NOT NULL DEFAULT '0',
  `user_name` varchar(255) DEFAULT NULL COMMENT '用户名称',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

