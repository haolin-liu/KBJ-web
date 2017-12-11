package solr.params;

import com.typesafe.config.Config;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.commons.lang3.StringUtils;
import play.Logger;

import javax.inject.Inject;
import java.util.List;

public class KeywordHelper {

    private static Config config;
    @Inject
    public KeywordHelper(Config config) {
        this.config = config;
    }

    /**
     * get target fields which a query will query from from conf setting.
     * @return
     */
    public static List<String> getQueryTargetFields() {
        List<String> fromFields = config.getStringList("solr.query.from.fl");
        if (fromFields.size() == 0) {
            fromFields.add("*");
        }
        return fromFields;
    }

    /**
     *
     * @param keyword
     * @return
     */
    public static String getQueryString(String keyword) {

        List<String> fromFields = getQueryTargetFields();
        String connector = config.getString("solr.query.keywords.connector");
        String[] kws = splitBySpaces(keyword);
//        Logger.debug("origin keyword: " + keyword);

        int i = 0;
        String escaped;
        StringBuilder sb = new StringBuilder();
        for (String fl : fromFields) {
            if (kws.length == 0) {
                sb.append(fl + ":*");
            } else {
                sb.append("(");
                int j = 0;
                for (String kw : kws) {
                    escaped = escapeSpecialChars(kw.trim());
                    sb.append(fl + ":" + escaped);
                    if ( j != kws.length - 1) {
                        sb.append(" " + connector + " ");
                    }
                    j++;
                }
                sb.append(")");
            }
            // connect clauses with OR
            if ( i != fromFields.size() - 1) {
                sb.append(" OR ");
            }
            i++;
        }

//        Logger.debug("after keyword: " + sb.toString());
        return sb.toString();
    }

    /**
     * notice:
     *   both the full-width and half-width spaces should be considered
     *   as separator.
     * @param keyword
     * @return
     */
    public static String[] splitBySpaces(String keyword) {
        // notice:
        // can't use the java split method of string:
        // for the spaces(half-width & full-width) in the tail can be ignored although,
        // the spaces in the head can't be ignored.
        // !!!
        // return keyword.trim().split("(　|\\s)+");
        return StringUtils.split(keyword);
    }

    /**
     * notice:
     * the special characters in solr is
     *   + - && || ! ( ) { } [ ] ^ " ~ * ? : \ and [space].
     *   for [space] both half-width and full-width space will be escaped.
     * should be converted in advance.
     * @param keyword
     * @return
     */
    public static String escapeSpecialChars(String keyword) {
        return ClientUtils.escapeQueryChars(keyword);
    }

}
