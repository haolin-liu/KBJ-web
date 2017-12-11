package solr.params;

import org.apache.commons.lang3.StringUtils;
import play.libs.F;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * lucene query string(s) for filtering the results without affecting scoring
 * format eg:
 *   (fq=)(+)price:[1 to 1000],(fq=)-sales:[1000 to *],(fq=)cate:mouse
 * @author jie-z
 * @date 2017/12/01
 */
public class QueryFilter {

    private final static String connector = ",";
    private final static String prefix = "fq=";
    private final static String range = " TO ";
    private final static String leftBracket = "[";
    private final static String rightBracket = "]";
    private final static String plus = "+";
    private final static String minus = "-";
    private final static String asterisk = "*";
    protected final static String msg = "wrong format of query filter in request parameters.\n" +
            "must be the format like:\n" +
            "    (fq=)(+)field1:[value1 TO value2] or (fq=)-field2:value1.\n" +
            "or the union of this two with the connector ','.\n" +
            "the prefix 'fq=' or '+' symbol of field can be omitted.\n";

    public enum DIRECTION { in, ex;
        public DIRECTION reverse() {
            return (this == in) ? ex : in;
        }
    }

    public QueryFilter(String direct, String field, String from, String to) throws Exception {
        setDirection(direct);
        setField(field);
        setFrom(from);
        setTo(to);
    }

    private DIRECTION direction = DIRECTION.in;
    private String field;
    private String from = asterisk;
    private String to = asterisk;

    public DIRECTION getDirection() {
        return direction;
    }

    public void setDirection(DIRECTION direction) {
        this.direction = direction;
    }

    public void setDirection(String direction) throws Exception {
        if (direction == null || direction.isEmpty()) {
            this.direction = DIRECTION.in;
        } else {
            if (direction.trim().toLowerCase().equals(DIRECTION.in.toString())
                    || direction.trim().equals(plus)) {
                this.direction = DIRECTION.in;
            } else if (direction.toLowerCase().equals(DIRECTION.ex.toString())
                    || direction.trim().equals(minus)) {
                this.direction = DIRECTION.ex;
            } else {
                throw new Exception(msg);
            }
        }
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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        if (from == null || from.trim().isEmpty()) {
            this.from = asterisk;
        } else {
            this.from = from;
        }
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        if (to == null || to.trim().isEmpty()) {
            this.to = asterisk;
        } else {
            this.to = to;
        }
    }

    /**
     * to string in the format: (-)field:[value1 TO value2]
     */
    @Override
    public String toString() {
        String direct = direction == DIRECTION.in ? "" : minus;
        return direct + this.field + ":" + leftBracket + this.from + range + this.to + rightBracket;
    }

    public static QueryFilter convertFromTuple(F.Tuple4<String, String, String, String> fq)
            throws Exception {
        if (fq != null) {
            return new QueryFilter(fq._1, fq._2, fq._3, fq._4);
        }
        return null;
    }

    public static List<QueryFilter> convertFromTuple(List<F.Tuple4<String, String, String, String>> filters)
            throws Exception {
        List<QueryFilter> fqs = new ArrayList<>();
        if (filters != null) {
            for (F.Tuple4<String, String, String, String> flts : filters) {
                QueryFilter fq = convertFromTuple(flts);
                if (fq != null) {
                    fqs.add(fq);
                }
            }
        }
        return fqs;
    }

    /**
     * apple the request parameter [fq]s to the solr query filter string.
     *   the format of fq in request parameters should be
     *     (fq=)(+)price:[1 TO 1000],(fq=)-cate:mouse
     * @param strFq
     * @return
     * @throws Exception
     */
    public static List<QueryFilter> apply(String strFq) throws Exception {

        List<QueryFilter> fqs = new ArrayList<>();
        String direct, key, values, value, value1, value2;
        String message = msg + "the request string is:\n" + strFq;

        String[] aryFq = StringUtils.split(strFq, connector);

        for (String f : aryFq) {
            String[] aryPair = StringUtils.split(f,":");
            if (aryPair.length != 2) {
                throw new Exception(message);
            } else {
                key = aryPair[0].trim();
                values = aryPair[1].trim();

                if (key.toLowerCase().startsWith(prefix)) {
                    // drop the prefix [fq=] in key.
                    key = key.substring(prefix.length());
                }
                // extract the direction [+/-/] of key.
                if (key.startsWith(plus) || key.startsWith(minus)) {
                    direct = key.substring(0, 1);
                    key = key.substring(1);
                } else {
                    direct = "";
                }

                // if there is no range in value, it is the value.
                if (values.toUpperCase().indexOf(range) == -1) {
                    value = values;
                    fqs.add(new QueryFilter(direct, key, value, value));
                } else {
                    // if there is range in value, split it to lower limit and upper limit.
                    String[] aryValue = StringUtils.split(values, range);
                    if (aryValue.length != 2) {
                        throw new Exception(message);
                    } else {
                        value1 = aryValue[0];
                        value2 = aryValue[1];
                        if (!value1.trim().startsWith(leftBracket)
                                || !value2.trim().endsWith(rightBracket)) {
                            throw new Exception(message);
                        } else {
                            value1 = value1.replace(leftBracket, "");
                            value2 = value2.replace(rightBracket, "");
                            fqs.add(new QueryFilter(direct, key, value1, value2));
                        }
                    }
                }
            }
        }
        return fqs;
    }

}
