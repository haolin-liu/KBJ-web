package models.form;

/**
 *  @author 吕
 *  @date  2017/11/29
 */
public class UserSearchForm {

    public int page;

    public String name;

    public String phone;

    public String email;

    public boolean isSearch = true;

    public UserSearchForm () {}

    public UserSearchForm(int page, String name, String phone, String email, boolean isSearch) {
        this.page = page;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.isSearch = isSearch;
    }
}
