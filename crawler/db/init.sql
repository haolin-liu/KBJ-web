create database if not exists kebja default charset utf8 collate utf8_general_ci;

create user kebja;

grant all privileges on kebja.* to kebja@localhost identified by 'kebja';

create table daily_price (
  id                            bigint auto_increment not null,
  create_date                   TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null,
  create_user                   varchar(100) DEFAULT 'system' not null,
  update_date                   TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null,
  update_user                   varchar(100) DEFAULT 'system' not null,
  mall                          varchar(255),
  skuid                         varchar(255),
  price                         decimal(15,2),
  ref_price                     decimal(15,2),
  discount                      decimal(15,2),
  discount_rate                 decimal(15,2),
  kbj_category_id               bigint,
  date                          date,
  timestamp                     TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null,
  constraint pk_daily_price primary key (id)
) DEFAULT CHARSET=utf8;

create table mall_category (
  id                            bigint auto_increment not null,
  create_date                   TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null,
  create_user                   varchar(100) DEFAULT 'system' not null,
  update_date                   TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null,
  update_user                   varchar(100) DEFAULT 'system' not null,
  name                          varchar(100),
  link                          varchar(500),
  mall                          varchar(30),
  tag                           varchar(500),
  mall_cat                      varchar(500),
  mall_sub                      varchar(500),
  mall_tid                      varchar(500),
  valid                         boolean default true,
  is_crawl_target               boolean default true,
  constraint pk_mall_category primary key (id)
) DEFAULT CHARSET=utf8;

update mall_category set valid=0 where id not in (2, 27, 14, 20, 41, 74, 205, 209, 215, 234, 229, 219, 116, 137, 138, 135, 143, 133);

-- 电视机 冰箱 空调 洗衣机 热水器 空气净化器
INSERT INTO category_mapping(id, kbj_category_id, mall_category_id) VALUES (1,2001,2);
INSERT INTO category_mapping(id, kbj_category_id, mall_category_id) VALUES (2,2002,27);
INSERT INTO category_mapping(id, kbj_category_id, mall_category_id) VALUES (3,2003,14);
INSERT INTO category_mapping(id, kbj_category_id, mall_category_id) VALUES (4,2004,20);
INSERT INTO category_mapping(id, kbj_category_id, mall_category_id) VALUES (5,2005,41);
INSERT INTO category_mapping(id, kbj_category_id, mall_category_id) VALUES (6,2006,74);
-- 笔记本电脑 平板电脑 显示器 移动硬盘 鼠标 内存条
INSERT INTO category_mapping(id, kbj_category_id, mall_category_id) VALUES (7,3001,205);
INSERT INTO category_mapping(id, kbj_category_id, mall_category_id) VALUES (8,3002,209);
INSERT INTO category_mapping(id, kbj_category_id, mall_category_id) VALUES (9,3003,215);
INSERT INTO category_mapping(id, kbj_category_id, mall_category_id) VALUES (10,3004,234);
INSERT INTO category_mapping(id, kbj_category_id, mall_category_id) VALUES (11,3005,229);
INSERT INTO category_mapping(id, kbj_category_id, mall_category_id) VALUES (12,3006,219);
-- 手机 耳机 充电器 内存卡 移动电源 手机壳&保护膜
INSERT INTO category_mapping(id, kbj_category_id, mall_category_id) VALUES (13,4001,116);
INSERT INTO category_mapping(id, kbj_category_id, mall_category_id) VALUES (14,4002,137);
INSERT INTO category_mapping(id, kbj_category_id, mall_category_id) VALUES (15,4003,138);
INSERT INTO category_mapping(id, kbj_category_id, mall_category_id) VALUES (16,4004,135);
INSERT INTO category_mapping(id, kbj_category_id, mall_category_id) VALUES (17,4005,143);
INSERT INTO category_mapping(id, kbj_category_id, mall_category_id) VALUES (18,4006,133);