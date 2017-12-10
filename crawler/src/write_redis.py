import MySQLdb
import MySQLdb.cursors
import ConfigParser
import redis
import sys

sys.path.append("../")
from crawler.settings import MYSQL_HOST, MYSQL_DBNAME, MYSQL_USER, MYSQL_PASSWD, REDIS_HOST, REDIS_PORT


class WriteRedis(object):
    def __init__(self):
        conf = ConfigParser.ConfigParser()
        conf.read('crawler/settings.py')
        self.db = MySQLdb.connect(host=MYSQL_HOST,
                                  user=MYSQL_USER,
                                  passwd=MYSQL_PASSWD,
                                  db=MYSQL_DBNAME,
                                  charset="utf8")

        pool = redis.ConnectionPool(host=REDIS_HOST,
                                    port=REDIS_PORT,
                                    decode_responses=True)
        self.redis = redis.Redis(connection_pool=pool)

    def read_from_sql(self):
        sql = "select k.id, k.name, mc.mall, mc.link, mc.mall_cat, mc.mall_sub, mc.mall_tid "
        sql += " from mall_category mc "
        sql += "   left join category_mapping cm on mc.id=cm.mall_cate_id "
        sql += "   left join kbj_category k on cm.kbj_cate_id=k.id "
        sql += " where mc.valid=1"
        sql += "   and mc.is_crawl_target=1;"
        cursor = self.db.cursor()
        cursor.execute(sql)
        rows = cursor.fetchall()
        self.db.cursor().close()
        self.db.close()
        return rows

    def write_to_redis(self, rows):
        self.redis.flushall()
        for row in rows:
            dic = {
                "id": row[0],
                "name": row[1]
            }

            key = row[4] if row[4] else row[5]
            key = key if key else row[6]

            self.redis.hmset(row[2] + key, dic)
            self.redis.lpush('jd_prod:start_urls', row[3])

    def handle_error(self, failue, item, spider):
        print failue


writer = WriteRedis()
rows = writer.read_from_sql()
writer.write_to_redis(rows)
