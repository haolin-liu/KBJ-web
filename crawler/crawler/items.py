# -*- coding: utf-8 -*-

# Define here the models for your scraped items
#
# See documentation in:
# http://doc.scrapy.org/en/latest/topics/items.html

import scrapy


class Category(scrapy.Item):
    id = scrapy.Field()
    name = scrapy.Field()
    link = scrapy.Field()
    mall = scrapy.Field()
    tag = scrapy.Field()
    mall_cat = scrapy.Field()
    mall_sub = scrapy.Field()
    mall_tid = scrapy.Field()
    pass


class Product(scrapy.Item):
    mall = scrapy.Field()
    skuid = scrapy.Field()
    item_id = scrapy.Field()
    name = scrapy.Field()
    url = scrapy.Field()
    kbj_cate_name = scrapy.Field()
    kbj_cate_id = scrapy.Field()
    mall_cate_url = scrapy.Field()
    price = scrapy.Field()
    ref_price = scrapy.Field()
    discount = scrapy.Field()
    discount_rate = scrapy.Field()
    sales_num = scrapy.Field()
    comments_num = scrapy.Field()
    stock_status = scrapy.Field()
    img1 = scrapy.Field()
    img2 = scrapy.Field()
    img3 = scrapy.Field()
    img4 = scrapy.Field()
    img1_max = scrapy.Field()
    img2_max = scrapy.Field()
    img3_max = scrapy.Field()
    img4_max = scrapy.Field()
    specs = scrapy.Field()
    shop = scrapy.Field()
    shop_url = scrapy.Field()
    is_self_support = scrapy.Field()
    date = scrapy.Field()
    pass
