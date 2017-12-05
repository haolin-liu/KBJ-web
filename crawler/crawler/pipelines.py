# -*- coding: utf-8 -*-
import re
# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: http://doc.scrapy.org/en/latest/topics/item-pipeline.html
import urlparse
from time import strftime
from scrapy.exceptions import DropItem
from twisted.enterprise import adbapi
import MySQLdb
import MySQLdb.cursors
import redis
from crawler.exporters import JsonFileItemExporter, CsvFileItemExporter


class CategoryPipeline(object):

    @classmethod
    def from_crawler(cls, crawler):
        return cls()

    def process_item(self, item, spider):
        pattern_all = re.compile('^(list|[0-9]).*$')
        pattern_no = re.compile('^[0-9].*$')

        if pattern_all.match(item['link']):
            # 有的分类信息只是分类id
            if pattern_no.match(item['link']):
                item['mall_cat'] = item['link']
                item['link'] = 'list.jd.com/list.html?cat=' + item['link']
        if item['link']:
            # 把-转换成,
            hyphen = re.compile('-')
            pattern = re.compile('^(list.jd.com/list.html\?cat=)[0-9]*-[0-9]*.*$')
            if pattern.match(item['link']):
                item['link'] = hyphen.sub(',', item['link'])

            # 存储到redis中的key
            item['mall_cat'] = urlparse.parse_qs(urlparse.urlsplit(item['link']).query).get('cat')
            item['mall_tid'] = urlparse.parse_qs(urlparse.urlsplit(item['link']).query).get('tid')
            item['mall_sub'] = urlparse.parse_qs(urlparse.urlsplit(item['link']).query).get('sub')
            item['link'] = 'http://' + item['link']

            query = spider.db_pool.runInteraction(self.insert, item)
            query.addErrback(self.handle_error, item, spider)
            return item
        else:
            # 有的父分类是空的直接删除
            raise DropItem("Duplicate item found: %s" % item)

    def insert(self, tx, item):
        timestamp = strftime("%Y-%m-%d %H:%M:%S")
        sql = "insert into mall_category "
        sql += " (name, link, mall, tag, valid, is_crawl_target,"
        sql += " mall_cat, mall_tid, mall_sub, "
        sql += " create_date, create_user, update_date, update_user) "
        sql += " values(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"
        params = (item['name'], item['link'], item['mall'], item['tag'], 1, 1,
                  item['mall_cat'], item['mall_tid'], item['mall_sub'],
                  timestamp, 'system', timestamp, 'system')
        tx.execute(sql, params)

    def handle_error(self, failue, item, spider):
        print failue


class ProductPipeline(object):
    def __init__(self, file_name, file_type):
        self.file_name = file_name
        self.file_type = file_type
        self.file_handle = None
        self.exporter = None

    @classmethod
    def from_crawler(cls, crawler):
        output_file_name = crawler.settings.get('JD_PROD_FILE_NAME')
        output_file_type = crawler.settings.get('CSV')

        return cls(output_file_name, output_file_type)

    def open_spider(self, spider):
        file = open(self.file_name + self.file_type, 'wb')
        self.file_handle = file

        self.exporter = CsvFileItemExporter(file)
        self.exporter.start_exporting()

    def close_spider(self, spider):
        self.exporter.finish_exporting()
        self.file_handle.close()

    def process_item(self, item, spider):
        self.exporter.export_item(item)
        return item


class ProductPricePipeline(object):

    @classmethod
    def from_crawler(cls, crawler):
        return cls()

    def process_item(self, item, spider):
        if item['ref_price']:
            item['discount'] = float(item['ref_price']) - float(item['price'])
            item['discount_rate'] = float(item['discount']) / float(item['ref_price'])
        query = spider.db_pool.runInteraction(self.insert, item)
        query.addErrback(self.handle_error, item, spider)
        return item

    def insert(self, tx, item):
        date = strftime("%Y-%m-%d")
        timestamp = strftime("%Y-%m-%d %H:%M:%S")

        sql = "insert into daily_price "
        sql += " (mall, skuid, price, ref_price, discount, discount_rate, "
        sql += " kbj_cate_id, `date`, `timestamp`, create_date, create_user, update_date, update_user) "
        sql += " values(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"
        params = (item['mall'], item['skuid'], item['price'], item['ref_price'], item['discount'], item['discount_rate'],
                  item['kbj_cate_id'], date, timestamp, timestamp, 'system', timestamp, 'system',)
        tx.execute(sql, params)

    def handle_error(self, failue, item, spider):
        print failue
