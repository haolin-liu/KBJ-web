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
  kbj_cate_id                   bigint,
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

update mall_category set valid=0 where id not in (1, 26, 12, 115, 211);
-- 电视 冰箱 空调 手机 显示器
INSERT INTO category_mapping(id, kbj_cate_id, mall_cate_id) VALUES (1,2001,1);
INSERT INTO category_mapping(id, kbj_cate_id, mall_cate_id) VALUES (2,2002,26);
INSERT INTO category_mapping(id, kbj_cate_id, mall_cate_id) VALUES (3,2003,12);
INSERT INTO category_mapping(id, kbj_cate_id, mall_cate_id) VALUES (4,4002,115);
INSERT INTO category_mapping(id, kbj_cate_id, mall_cate_id) VALUES (5,3003,211);