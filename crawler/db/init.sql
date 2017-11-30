create database if not exists kebja default charset utf8 collate utf8_general_ci;

create user kebja;

grant all privileges on kebja.* to kebja@localhost identified by 'kebja';

CREATE TABLE `daily_price` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `mall` varchar(30) NOT NULL,
  `skuid` varchar(50) NOT NULL,
  `price` decimal(15,2) NOT NULL,
  `ref_price` decimal(15,2) NOT NULL,
  `date` datetime NOT NULL,
  `timestamp` datetime NOT NULL,
  `create_date` datetime NOT NULL DEFAULT NOW(),
  `create_user` varchar(50) NOT NULL DEFAULT 'system',
  `update_date` datetime NOT NULL DEFAULT NOW(),
  `update_user` varchar(50) NOT NULL DEFAULT 'system',
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8;

CREATE TABLE `mall_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `link` varchar(500) NOT NULL,
  `mall` varchar(30) NOT NULL,
  `tag` varchar(500) NOT NULL,
  `valid` boolean NOT NULL,
  `create_date` datetime NOT NULL DEFAULT NOW(),
  `create_user` varchar(50) NOT NULL DEFAULT 'system',
  `update_date` datetime NOT NULL DEFAULT NOW(),
  `update_user` varchar(50) NOT NULL DEFAULT 'system',
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8;