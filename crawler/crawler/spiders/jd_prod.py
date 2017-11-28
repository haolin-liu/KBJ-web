# coding=utf-8
import json
import urlparse
import scrapy

from time import strftime
from scrapy.spiders import CrawlSpider, Rule
from scrapy.linkextractors import LinkExtractor
from crawler.items import Product


class JDProductSpider(CrawlSpider):
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

    def __init__(self, cat_path, cat_id, cat_name, *args, **kwargs):
        super(JDProductSpider, self).__init__(*args, **kwargs)
        self.cat_name = cat_name
        file = open(cat_path, "r")
        data = json.load(file, encoding="utf-8")["data"]
        for item in data:
            if item['id'] == int(cat_id):
                self.start_urls.append("http://" + item['link'])

    def parse_start_url(self, response):
        products = response.xpath('//li[@class="gl-item"]')
        for product in products:
            sku_id = product.xpath('./div/@data-sku').extract_first()
            item_url = product.xpath('./div/div[@class="p-img"]/a/@href').extract_first()
            if item_url:
                yield scrapy.Request("http:" + item_url,
                                     callback=self.parse_detail,
                                     meta={'sku_id': sku_id, 'category_url': response.url, 'item_url': item_url})

    def parse_detail(self, response):
        item = Product()
        sku_id = response.meta['sku_id']

        item['mall'] = 'jd'
        item['sku_id'] = sku_id
        item['name'] = response.xpath('string(//div[@class="sku-name"])').extract_first().strip()
        item['url'] = "http:" + response.meta['item_url']
        item['kbj_cate_name'] = 'tv'
        item['kbj_cate_id'] = '1'
        item['mall_cate_url'] = response.meta['category_url']
        # item['is_in_stock'] = 1 if response.xpath('string(//div[@id="store - prompt"]/strong)').extract_first() else 0
        item['is_in_stock'] = response.xpath('//div[@class="store-prompt"]/strong').extract_first()
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

        price_url = "http://pm.3.cn/prices/pcpmgets?callback=jQuery&skuids=" + sku_id + "&origin=2"
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

        url = "http://club.jd.com/clubservice.aspx?method=GetCommentsCount&referenceIds=" + str(item['sku_id'])
        yield scrapy.Request(url, meta={'item': item}, callback=self.parse_comment_num)

    def parse_comment_num(self, response):
        item = response.meta['item']
        js = json.loads(str(response.body.decode("gbk").encode("utf-8")))
        item['comments_num'] = js['CommentsCount'][0]['CommentCount']

        cat = urlparse.parse_qs(urlparse.urlsplit(item['mall_cate_url']).query).get('cat')
        if cat:
            url = 'http://c0.3.cn/stock?skuId=' + item[
                'sku_id'] + '&area=1_72_4137_0&cat=' + cat[0] + '&choseSuitSkuIds=&extraParam={"originid":"1"}'
            yield scrapy.Request(url, meta={'item': item}, callback=self.parse_stock)

    def parse_stock(self, response):
        item = response.meta['item']
        js = json.loads(str(response.body.decode("gbk").encode("utf-8")))
        item['is_in_stock'] = js['stock']['stockDesc']

        return item
