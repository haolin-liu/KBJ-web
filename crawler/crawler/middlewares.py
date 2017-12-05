# -*- coding: utf-8 -*-

# Define here the models for your spider middleware
#
# See documentation in:
# http://doc.scrapy.org/en/latest/topics/spider-middleware.html
import random

import MySQLdb
import redis
from scrapy import signals
from twisted.enterprise import adbapi


class AgentMiddleware(object):
    # Not all methods need to be defined. If a method is not defined,
    # scrapy acts as if the spider middleware does not modify the
    # passed objects.

    def __init__(self, setting, user_agent=''):
        self.user_agent = user_agent
        user_agent_list_file = setting.get('AGENT_FILE_NAME')
        with open(user_agent_list_file, 'r') as f:
            self.user_agent_list = [line.strip() for line in f.readlines()]

    @classmethod
    def from_crawler(cls, crawler):
        # This method is used by Scrapy to create your spiders.
        s = cls(crawler.settings)
        crawler.signals.connect(s.spider_opened, signal=signals.spider_opened)
        return s

    def process_start_requests(self, start_requests, spider):
        # Called with the start requests of the spider, and works
        # similarly to the process_spider_output() method, except
        # that it doesn’t have a response associated.

        # Must return only requests (not items).
        for r in start_requests:
            ua = random.choice(self.user_agent_list)
            if ua:
                spider.logger.info('Spider user agent : %s' % ua)
                r.headers.setdefault('User-Agent', ua)
            yield r

    def spider_opened(self, spider):
        spider.logger.info('Spider user agent opened: %s' % spider.name)


class ProxyMiddleware(object):
    def __init__(self, setting, ip=''):
        self.ip = ip
        proxy_file = setting.get('PROXY_FILE_NAME')
        with open(proxy_file, 'r') as f:
            self.ip_pool = [line.strip() for line in f.readlines()]

    @classmethod
    def from_crawler(cls, crawler):
        # This method is used by Scrapy to create your spiders.
        s = cls(crawler.settings)
        crawler.signals.connect(s.spider_opened, signal=signals.spider_opened)
        return s

    def process_start_requests(self, start_requests, spider):
        # Called with the start requests of the spider, and works
        # similarly to the process_spider_output() method, except
        # that it doesn’t have a response associated.

        # Must return only requests (not items).
        for r in start_requests:
            ip = random.choice(self.ip_pool)
            if ip:
                spider.logger.info('Spider http proxy : %s' % "http://" + ip)
                r.meta["proxy"] = "http://" + ip
            yield r

    def spider_opened(self, spider):
        spider.logger.info('Spider http proxy opened: %s' % spider.name)


class DBMiddleware(object):
    def __init__(self):
        pass

    @classmethod
    def from_crawler(cls, crawler):
        o = cls()
        crawler.signals.connect(o.spider_opened, signal=signals.spider_opened)
        return o

    def spider_opened(self, spider):
        params = dict(
            host=spider.settings['MYSQL_HOST'],
            db=spider.settings['MYSQL_DBNAME'],
            user=spider.settings['MYSQL_USER'],
            passwd=spider.settings['MYSQL_PASSWD'],
            charset='utf8',
            cursorclass=MySQLdb.cursors.DictCursor,
            use_unicode=True
        )
        spider.db_pool = adbapi.ConnectionPool('MySQLdb', **params)
