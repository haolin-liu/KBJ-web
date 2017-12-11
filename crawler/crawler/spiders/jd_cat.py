import json
import scrapy

from crawler.items import Category


class JDCategorySpider(scrapy.Spider):
    name = "jd_cat"
    allowed_domains = ["jd.com"]
    custom_settings = {
        'ITEM_PIPELINES': {
            'crawler.pipelines.CategoryPipeline': 300
        }
    }

    def start_requests(self):
        return [scrapy.Request("https://dc.3.cn/category/get?callback=getCategoryCallback",
                               callback=self.parse)]

    def parse(self, response):
        temp = response.body.split("getCategoryCallback(")[1][:-1].decode("gbk").encode("utf-8")
        data = json.loads(temp)['data']

        for item in data:
            for root in item['s']:
                arr = root['n'].split("|")
                root_tag = arr[1]
                yield self.parse_item(arr, id, root_tag)
                for parent in root['s']:
                    arr = parent['n'].split("|")
                    p_tag = "," + arr[1]
                    yield self.parse_item(arr, id, root_tag + p_tag)
                    for category in parent['s']:
                        arr = category['n'].split("|")
                        tag = "," + arr[1]
                        yield self.parse_item(arr, id, root_tag + p_tag + tag)

    @staticmethod
    def parse_item(arr, id, tag=''):
        category = Category()
        category['id'] = id
        category['mall'] = 'jd'
        category['link'] = arr[0]
        category['name'] = arr[1]
        category['tag'] = tag
        return category
