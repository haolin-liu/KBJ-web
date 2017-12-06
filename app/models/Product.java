package models;

import org.apache.solr.client.solrj.beans.Field;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import java.util.Formatter;

//@Entity
//public class Product extends Model {
public class Product {

    public static class SolrField {
        public final static String mall = "mall";
        public final static String skuid = "skuid";
        public final static String item_id = "item_id";
        public final static String name = "name";
        public final static String url = "url";
        public final static String kbj_cate_name = "kbj_cate_name";
        public final static String kbj_cate_id = "kbj_cate_id";
        public final static String mall_cate_url = "mall_kbj_url";
        public final static String price = "price";
        public final static String ref_price = "ref_price";
        public final static String sales_num = "sales_num";
        public final static String comments_num = "comments_num";
        public final static String stock_status = "stock_status";
        public final static String img1 = "img1";
        public final static String img2 = "img2";
        public final static String img3 = "img3";
        public final static String img4 = "img4";
        public final static String img1_max = "img1_max";
        public final static String img2_max = "img2_max";
        public final static String img3_max = "img3_max";
        public final static String img4_max = "img4_max";
        public final static String specs = "specs";
        public final static String shop = "shop";
        public final static String shop_url = "shop_url";
        public final static String self_support = "is_self_support";
        public final static String date = "date";
    }

    @Field(SolrField.mall)
    public String mall;

    @Field(SolrField.skuid)
    public String skuid;

    @Field(SolrField.item_id)
    public String itemId;

    @Field(SolrField.name)
    public String name;

    @Field(SolrField.url)
    public String url;

    @Field(SolrField.kbj_cate_name)
    public String kbjCateName;

    @Field(SolrField.kbj_cate_id)
    public String kbjCateId;

    @Field(SolrField.mall_cate_url)
    public String mallCateUrl;

    @Field(SolrField.price)
    public float price;

    @Field(SolrField.ref_price)
    public float refPrice;

    @Field(SolrField.sales_num)
    public String salesNum;

    @Field(SolrField.comments_num)
    public String commentsNum;

    @Field(SolrField.stock_status)
    public String stockStatus;

    @Field(SolrField.img1)
    public String img1;

    @Field(SolrField.img2)
    public String img2;

    @Field(SolrField.img3)
    public String img3;

    @Field(SolrField.img4)
    public String img4;

    @Field(SolrField.img1_max)
    public String img1Max;

    @Field(SolrField.img2_max)
    public String img2Max;

    @Field(SolrField.img3_max)
    public String img3Max;

    @Field(SolrField.img4_max)
    public String img4Max;

    @Field(SolrField.specs)
    public String specs;

    @Field(SolrField.shop)
    public String shop;

    @Field(SolrField.shop_url)
    public String shopUrl;

    @Field(SolrField.self_support)
    public boolean selfSupport;

    @Field(SolrField.date)
    private String date;

    public String getMall() {
        return mall;
    }

    public void setMall(String mall) {
        this.mall = mall;
    }

    public String getSkuid() {
        return skuid;
    }

    public void setSkuid(String skuid) {
        this.skuid = skuid;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKbjCateName() {
        return kbjCateName;
    }

    public void setKbjCateName(String kbjCateName) {
        this.kbjCateName = kbjCateName;
    }

    public String getKbjCateId() {
        return kbjCateId;
    }

    public void setKbjCateId(String kbjCateId) {
        this.kbjCateId = kbjCateId;
    }

    public String getMallCateUrl() {
        return mallCateUrl;
    }

    public void setMallCateUrl(String mallCateUrl) {
        this.mallCateUrl = mallCateUrl;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getRefPrice() {
        return refPrice;
    }

    public void setRefPrice(float refPrice) {
        this.refPrice = refPrice;
    }

    public String getSalesNum() {
        return salesNum;
    }

    public void setSalesNum(String salesNum) {
        this.salesNum = salesNum;
    }

    public String getCommentsNum() {
        return commentsNum;
    }

    public void setCommentsNum(String commentsNum) {
        this.commentsNum = commentsNum;
    }

    public String getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getImg3() {
        return img3;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }

    public String getImg4() {
        return img4;
    }

    public void setImg4(String img4) {
        this.img4 = img4;
    }

    public String getImg1Max() {
        return img1Max;
    }

    public void setImg1Max(String img1Max) {
        this.img1Max = img1Max;
    }

    public String getImg2Max() {
        return img2Max;
    }

    public void setImg2Max(String img2Max) {
        this.img2Max = img2Max;
    }

    public String getImg3Max() {
        return img3Max;
    }

    public void setImg3Max(String img3Max) {
        this.img3Max = img3Max;
    }

    public String getImg4Max() {
        return img4Max;
    }

    public void setImg4Max(String img4Max) {
        this.img4Max = img4Max;
    }

    public String getSpecs() {
        return specs;
    }

    public void setSpecs(String specs) {
        this.specs = specs;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public String getShopUrl() {
        return shopUrl;
    }

    public void setShopUrl(String shopUrl) {
        this.shopUrl = shopUrl;
    }

    public boolean isSelfSupport() {
        return selfSupport;
    }

    public void setSelfSupport(boolean selfSupport) {
        this.selfSupport = selfSupport;
    }

    public String getString() {
        return date;
    }

    public void setString(String date) {
        this.date = date;
    }

}
