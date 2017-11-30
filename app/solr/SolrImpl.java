package solr;

import com.typesafe.config.Config;
import models.Product;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import play.Logger;
import play.libs.F;
import solr.params.KeywordHelper;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SolrImpl implements SolrI {

    // TODO
    /*
     * defType	指定用于处理查询语句(参数q的内容)的查询解析器，eg:defType=lucene
     * sort	指定响应的排序方式：升序asc或降序desc.同时需要指定按哪个字段进行排序。eg: sort=price desc,score asc
     * start	指定显示查询结果的开始位置，默认是0
     * rows	指定一次显示多少行查询结果，默认是10
     * fq	指定用于对查询结果进行过滤的过滤器(也看作是一种query) eg: fq=price:[100 To *]&fq=setction:0
     * fl	指定查询结果中返回的字段，该字段的stored=”true”或docValues=”true” ,eg:fl=id,title,product(price, popularity)
     * debug	指定查询结果中携带额外的调试信息：时间信息debug=timing，“explain”信息debug=results,所有调试信息debug=query
     * explainOther	Allows clients to specify a Lucene query to identify a set of documents. If non-blank,the explain info of each document which matches this query, relative to the main query (specified by the q parameter) will be returned along with the rest of the debugging information.
     * timeAllowed	指定查询处理的时间，单位毫秒。如果查询在指定的时间未完成，则只返回部分信息
     * segmentTerminateEarly	Indicates that, if possible, Solr should stop collecting documents from each individual (sorted) segment once it can determine that any subsequent documents in that segment will not be candidates for the rows being returned. The default is false.
     * omitHeader	当设为true时，返回结果不包含头部信息(例如请求花费的时间等信息)，默认是false
     * wt	执行响应的输出格式：xml或json等
     * logParamsList	指定哪些参数需要写入log, eg:logParamsList=q,fq
     * echoParams	指定响应头部包含哪些参数，取值为none/all/explicit(默认值)
     */

//    private final static String SERVER_CONF_KEY = "solr.server";
//    private final static String SHARDS_CONF_KEY = "solr.shards";
//    private final static String CONN_TIMEOUT_CONF_KEY = "solr.connection.timeout";
//    private final static String SOCKET_TIMEOUT_CONF_KEY = "solr.socket.timeout";

    // 分页，每页显示数量的默认值
    private final static int DEFAULT_QUERY_ROWS = 10;
    private final static String PRODUCTS = "products";

    private static HttpSolrClient solrClient;
//    private static String server;
//    private static List<String> shards;
//    private static int connectionTimeout;
//    private static int socketTimeout;


    private final Config config;
    private final KeywordHelper keywordHelper;

    @Inject
    public SolrImpl(Config config, KeywordHelper keywordHelper) {
        this.config = config;
        this.keywordHelper = keywordHelper;
        init();
    }

    protected void init() {
//        String server = this.config.getString(SERVER_CONF_KEY);
//        List<String> shards = this.config.getStringList(SHARDS_CONF_KEY);
//        int connectionTimeout = this.config.getInt(CONN_TIMEOUT_CONF_KEY);
//        int socketTimeout = this.config.getInt(SOCKET_TIMEOUT_CONF_KEY);
        String server = this.config.getString("solr.server");
        List<String> shards = this.config.getStringList("solr.shards");
        int connectionTimeout = this.config.getInt("solr.connection.timeout");
        int socketTimeout = this.config.getInt("solr.socket.timeout");

        this.solrClient = new HttpSolrClient.Builder(server)
                .withConnectionTimeout(connectionTimeout)
                .withSocketTimeout(socketTimeout)
                .build();
    }

    // TODO
    // should we release the solr connection?

    @Override
    public List<Product> query(String keyword, int start, int rows, List<F.Tuple<String, Integer>> sorters, String fq) {

        SolrQuery query = new SolrQuery();

        query.setQuery(keywordHelper.getQueryString(keyword));
        query.setStart(start);
        if (rows == 0) {
            query.setRows(DEFAULT_QUERY_ROWS);
        }

        for (F.Tuple<String, Integer> sorter : sorters) {
            SolrQuery.ORDER order = sorter._2 == 1 ? SolrQuery.ORDER.asc : SolrQuery.ORDER.desc;
            query.setSort(sorter._1, order);
        }

        query.setFilterQueries(fq);

        return doQuery(PRODUCTS, query);
    }


    @Override
    public List<Product> query(String keyword, String shop, int start, int rows, String sort, String fq) {
        Product product = new Product();
        List<Product> products = new ArrayList<>();
        products.add(product);
        return products;
    }

    @Override
    public List<Product> query(List<F.Tuple<String, String>> mallSquidPair, int start, int rows, String sort, String fq) {
        Product product = new Product();
        List<Product> products = new ArrayList<>();
        products.add(product);
        return products;
    }

    @Override
    public Product query(String mall, String skuid) {
        SolrQuery query = new SolrQuery();
        query.setQuery("mall:" + mall);
        query.setQuery("skuid:" + skuid);

        // todo
        // if there is no result.....
        return doQuery(PRODUCTS, query).get(0);
    }

    @Override
    public Product queryByUrl(String url) {
        Product product = new Product();
        return product;
    }

    @Override
    public List<Product> queryByName(String name, int start, int rows, String sort) {
        Product product = new Product();
        List<Product> products = new ArrayList<>();
        products.add(product);
        return products;
    }

    /**
     * query from solr.
     * @param collection
     * @param query
     * @return
     */
    protected List<Product> doQuery(String collection, SolrQuery query) {

        try {

            List<String> fields = keywordHelper.getQueryResponseFields();
            for (String fl : fields) {
                query.addField(fl);
            }

            QueryResponse response = this.solrClient.query(collection, query);
            // todo
            return response.getBeans(Product.class);

        } catch(SolrServerException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

//    /**
//     * get fields which will be return as a query result from conf setting.
//     * @return
//     */
//    protected List<String> getQueryResponseFields() {
//        List<String> fields = config.getStringList("solr.query.fl");
//        return fields;
//    }
//
//    /**
//     * get target fields which a query will query from from conf setting.
//     * @return
//     */
//    protected List<String> getQueryTargetFields() {
//        List<String> fromFields = config.getStringList("solr.query.from.fl");
//        if (fromFields.size() == 0) {
//            fromFields.add("*");
//        }
//        return fromFields;
//    }
//
//
//    protected String getQueryString(String keyword) {
//        Logger.debug("origin keyword: " + keyword);
//
//        List<String> fromFields = getQueryTargetFields();
//        String[] kws = splitBySpaces(keyword);
//
//        int i = 0;
//        String escaped;
//        StringBuilder sb = new StringBuilder();
//        for (String fl : fromFields) {
//            if (kws.length == 0) {
//                sb.append(fl + ":*");
//            } else {
//                sb.append("(");
//                int j = 0;
//                for (String kw : kws) {
//                    // todo
//                    // must test for
//                    // blanks in middle, head, and tail.
//                    escaped = escapeSpecialChars(kw.trim());
//                    Logger.debug("escaped keyword: " + escaped);
//                    sb.append(fl + ":*" + escaped + "*");
//                    if ( j != kws.length - 1) {
//                        // todo
////                        sb.append(" AND ");
//                        sb.append(" OR ");
//                    }
//                    i++;
//                }
//                sb.append(")");
//            }
//
//            if ( i != fromFields.size() - 1) {
//                sb.append(" OR ");
//            }
//            i++;
//        }
//
//        Logger.debug("after keyword: " + sb.toString());
//        return sb.toString();
//    }
//
//    /**
//     * notice:
//     *   both the full-width and half-width spaces should be considered
//     *   as separator.
//     * @param keyword
//     * @return
//     */
//    protected String[] splitBySpaces(String keyword) {
//        return keyword.split("(　|\\s)+");
//    }
//
//    /**
//     * notice:
//     * the special characters in solr is
//     *  + – && || ! ( ) { } [ ] ^ " ~ * ? : \ and [space].
//     * should be converted in advance.
//     * @param keyword
//     * @return
//     */
//    protected String escapeSpecialChars(String keyword) {
//        return ClientUtils.escapeQueryChars(keyword);
//    }

}
