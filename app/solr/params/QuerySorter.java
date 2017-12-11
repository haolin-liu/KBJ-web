package solr.params;

import org.apache.commons.lang3.StringUtils;
import play.libs.F;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * lucene query string(s) for sorting the results
 * format eg:
 *   field1 asc, field2 desc
 * @author jie-z
 * @date 2017/12/01
 */
public class QuerySorter {

    private final static String connector = ",";
    private final static String prefix = "sort=";
    protected final static String msg = "wrong format of query sorter in request parameters.\n" +
            "must be the format like:\n" + "" +
            "    field1 asc or field2 desc.\n" +
            "or the union of this two with the connector ','.\n" +
            "both the field and the order [asc/desc] should not be omitted.\n";


    public enum ORDER { asc, desc;
        public ORDER reverse() {
            return (this == asc) ? desc : asc;
        }
    }

    private String field;
    private ORDER order;

    public QuerySorter(String field, String order) throws Exception {
        setField(field);
        setOrder(order);
    }

    public String getField() {
        return field;
    }

    public void setField(String field) throws Exception {
        if (field == null || field.trim().isEmpty()) {
            throw new Exception(msg);
        } else {
            this.field = field;
        }
    }

    public ORDER getOrder() {
        return order;
    }

    public void setOrder(String order) throws Exception {
        if (order == null || order.isEmpty()) {
            this.order = ORDER.asc;
        } else {
            if (order.trim().toLowerCase().equals(ORDER.asc.toString())) {
                this.order = ORDER.asc;
            } else if (order.toLowerCase().equals(ORDER.desc.toString())) {
                this.order = ORDER.desc;
            } else {
                throw new Exception(msg);
            }
        }
    }

    @Override
    public String toString() {
        return this.field + " " + this.order;
    }

    public static QuerySorter convertFromTuple(F.Tuple<String, String> sort)
            throws Exception {
        if (sort != null) {
            return new QuerySorter(sort._1, sort._2);
        }
        return null;
    }

    public static List<QuerySorter> convertFromTuple(List<F.Tuple<String, String>> sorters)
            throws Exception {
        List<QuerySorter> sorts = new ArrayList<>();
        if (sorters != null) {
            for (F.Tuple<String, String> sts : sorters) {
                QuerySorter st = convertFromTuple(sts);
                if (st != null) {
                    sorts.add(st);
                }
            }
        }
        return sorts;
    }

    /**
     * apple the request parameter sort to the solr query filter string.
     *   the format of sort in request parameters should be
     *     (sort=)price:asc,cate:mouse
     * @param strSoter
     * @return
     */
    public static List<QuerySorter> apply(String strSoter) throws Exception {
        List<QuerySorter> sorters = new ArrayList<>();
        String message = msg + "the request string is:\n" + strSoter;

        // drop the prefix "sort="
        if (strSoter.trim().startsWith(prefix)) {
            strSoter = strSoter.replace(prefix, "");
        }

        String[] arySort = StringUtils.split(strSoter, connector);

        for (String sorts : arySort) {
            String[] aryPair = StringUtils.split(sorts);
            if (aryPair.length != 2) {
                throw new Exception(message);
            } else {
                String field = aryPair[0];
                String order = aryPair[1];
                sorters.add(new QuerySorter(field, order));
            }
        }

        return sorters;
    }

}
