package utils;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import play.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class ConfigUtil {

    private static Config config;
    @Inject
    public ConfigUtil(Config config) {
        this.config = config;
    }

    protected <T> T getVal(String key, T defVal, Class<T> cls) {
        try {
            if (!config.hasPath(key)) {
                throw new ConfigException.Missing(key);
            }

            Object val = config.getAnyRef(key);
            if ((val instanceof List) || cls.isArray()) {
                throw new Exception("the value set in conf file is a list.");
            } else {
                if (cls.isInstance(val)) {
                    return (T) val;
                } else {
                    throw new ConfigException.BadValue(key, "the value set in conf file is: " + val);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error("error occurred in config file. set default value: "  + defVal + " to " + key);
            return defVal;
        }
    }

    protected <T> List<T> getValList(String key, List<T> defVal, Class<T> cls) {
        try {
            if (!config.hasPath(key)) {
                throw new ConfigException.Missing(key);
            }

            Object val = config.getAnyRef(key);
            if (!(val instanceof List)) {
                throw new Exception("the value set in conf file is not a list.");
            } else {
                List<? extends Object> vals = config.getAnyRefList(key);
                List<T> realVals = new ArrayList<>();
                for (Object v : vals) {
                    if (cls.isInstance(v)) {
                        realVals.add((T) v);
                    } else {
                        throw new ConfigException.BadValue(key, "the value set in conf file is: " + val);
                    }
                }
                return realVals;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error("error occurred in config file. set default value: "  + defVal + " to " + key);
            return defVal;
        }
    }

    public Integer getCountOfMainCatesForBargain() {
        final String key = "webapp.mainpage.bargain.maincates.count";
        final int defVal = 5;
        return getVal(key, defVal, Integer.class);
    }

    public Integer getCountOfMinorCatesForBargain() {
        final String key = "webapp.mainpage.bargain.minorcates.count";
        final int defVal = 5;
        return getVal(key, defVal, Integer.class);
    }

    public Integer getOffsetMonthOfPriceHis() {
        final String key = "webapp.pricehis.offset.month";
        final int defVal = 15;
        return getVal(key, defVal, Integer.class);
    }

    public Integer getPointsOfXaxisOfPriceHis() {
        final String key = "webapp.pricehis.plot.x.axis.points";
        final int defVal = 6;
        return getVal(key, defVal, Integer.class);
    }

    public Integer getPagesizeOfProducts() {
        final String key = "webapp.productspage.pagesize";
        final int defVal = 12;
        return getVal(key, defVal, Integer.class);
    }

}
