package models.form;

import io.ebean.Model;

public class MallCategoryForm  extends Model{

    public int page;

    public Long id;

    public String name;

    public String link;

    public String mall;

    public String tag;

    public Boolean valid;

    public Boolean isCrawleTarget;

    public boolean isSearch;

}
