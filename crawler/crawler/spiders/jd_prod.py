# coding=utf-8
import json
import urlparse

import redis
import scrapy

from time import strftime
from scrapy.spiders import Rule
from scrapy.linkextractors import LinkExtractor
from scrapy_redis.spiders import RedisCrawlSpider
from scrapy.utils.project import get_project_settings

from crawler.items import Product


class JDProductSpider(RedisCrawlSpider):
    name = 'jd_prod'
    allow_domains = ["list.jd.com", "item.jd.com"]
    start_urls = []
    custom_settings = {
        'ITEM_PIPELINES': {
            'crawler.pipelines.ProductPipeline': 400,
            'crawler.pipelines.ProductPricePipeline': 300
        }
    }

    rules = (
        Rule(LinkExtractor(allow=(), restrict_xpaths=('//a[@class="pn-next"]',)), callback='parse_start_url',
             follow=True),
    )

    def __init__(self, *args, **kwargs):
        super(JDProductSpider, self).__init__(*args, **kwargs)
        settings = get_project_settings()
        pool = redis.ConnectionPool(host=settings['REDIS_HOST'],
                                    port=settings['REDIS_PORT'],
                                    decode_responses=True)
        self.redis = redis.Redis(connection_pool=pool)

    def parse_start_url(self, response):
        req_url = response.request.url
        cat = urlparse.parse_qs(urlparse.urlsplit(req_url).query).get('cat')
        tid = urlparse.parse_qs(urlparse.urlsplit(req_url).query).get('tid')
        sub = urlparse.parse_qs(urlparse.urlsplit(req_url).query).get('sub')
        redis_key = cat[0] if cat else tid
        redis_key = 'jd' + (redis_key if redis_key else sub[0])
        products = response.xpath('//li[@class="gl-item"]')
        for product in products:
            skuid = product.xpath('./div/@data-sku').extract_first()
            item_url = product.xpath('./div/div[@class="p-img"]/a/@href').extract_first()
            if item_url:
                yield scrapy.Request("http:" + item_url,
                                     callback=self.parse_detail,
                                     meta={'skuid': skuid, 'req_url': req_url, 'redis_key': redis_key, 'item_url': item_url})

    def parse_detail(self, response):
        item = Product()
        skuid = response.meta['skuid']
        category_url = response.meta['req_url']
        redis_key = response.meta['redis_key']

        item['mall'] = 'jd'
        item['skuid'] = skuid
        item['name'] = response.xpath('string(//div[@class="sku-name"])').extract_first().strip()
        item['url'] = "http:" + response.meta['item_url']
        item['kbj_cate_id'] = self.redis.hget(redis_key, 'id')
        item['kbj_cate_name'] = self.redis.hget(redis_key, 'name')
        item['mall_cate_url'] = category_url
        item['stock_status'] = response.xpath('//div[@class="store-prompt"]/strong').extract_first()
        # imgs
        imgs = response.xpath('//div[@id="spec-list"]/ul/li')
        i = 1
        for img in imgs:
            if i < 5:
                index = 'img' + str(i)
                value = "http:" + img.xpath('./img/@src').extract_first()
                item[index] = value
                index_max = index + '_max'
                value_max = str(value).replace('.com/n5', '.com/n1')
                item[index_max] = value_max
                i += 1
            else:
                break
        # spec
        p_tables = response.xpath('//div[@class="Ptable-item"]')
        specs = 'specs: {'
        for p_table in p_tables:
            key = p_table.xpath('string(./h3)').extract_first()
            value = ''
            for dt in p_table.xpath('./dl/dt'):
                dt_key = dt.xpath('string(.)').extract_first().strip()
                dt_value = dt.xpath('string(following-sibling::dd[1])').extract_first().strip()
                value += dt_key + ":" + dt_value + ","
            specs += key + ":{" + value[:-1] + "}"
        specs += "}"
        item['specs'] = specs
        shop_temp = response.xpath('//div[@class="J-hove-wrap EDropdown fr"]/div/div')
        item['shop'] = shop_temp.xpath('string(./a)').extract_first()
        if shop_temp.xpath('./a/@href'):
            item['shop_url'] = "http:" + shop_temp.xpath('./a/@href').extract_first()
        item['is_self_support'] = 1 if shop_temp.xpath('./em[@class="u-jd"]').extract() else 0
        item['date'] = strftime("%Y-%m-%d %H:%M:%S")

        price_url = "http://pm.3.cn/prices/pcpmgets?callback=jQuery&skuids=" + skuid + "&origin=2"
        yield scrapy.Request(price_url,
                             callback=self.parse_price,
                             meta={'item': item})

    def parse_price(self, response):
        item = response.meta['item']
        # price
        temp = response.body.split('jQuery([')[1][:-4].decode("gbk").encode("utf-8")
        js = json.loads(temp)
        print js
        if js.has_key('pcp'):
            item['price'] = js['pcp']
        else:
            item['price'] = js['p']
        if js.has_key('m'):
            item['ref_price'] = js['m']

        url = "http://club.jd.com/clubservice.aspx?method=GetCommentsCount&referenceIds=" + str(item['skuid'])
        yield scrapy.Request(url, meta={'item': item}, callback=self.parse_comment_num)

    def parse_comment_num(self, response):
        item = response.meta['item']
        js = json.loads(str(response.body.decode("gbk").encode("utf-8")))
        item['comments_num'] = js['CommentsCount'][0]['CommentCount']

        cat = urlparse.parse_qs(urlparse.urlsplit(item['mall_cate_url']).query).get('cat')
        if cat:
            url = 'http://c0.3.cn/stock?skuId=' + item[
                'skuid'] + '&area=1_72_4137_0&cat=' + cat[0] + '&choseSuitSkuIds=&extraParam={"originid":"1"}'
            yield scrapy.Request(url, meta={'item': item}, callback=self.parse_stock)

    def parse_stock(self, response):
        item = response.meta['item']
        js = json.loads(str(response.body.decode("gbk").encode("utf-8")))
        item['stock_status'] = js['stock']['stockDesc']

        return item
