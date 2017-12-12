package models.entities;

import play.data.validation.Constraints;

import javax.persistence.*;

/**
 * 管理者表
 *
 * @author lichen
 * @date 2017.12.7
 */
@Constraints.Validate
@Entity
public class Admin extends BaseModel implements Constraints.Validatable<String> {

    private final static String ERROR_EMPTY = "用户名或密码不能为空！";
    private final static String ERROR_LOGIN = "用户名或密码不匹配！";

    @Id
    public long userId;

    @Column(nullable = false, length = 20)
    public String username;

    @Column(nullable = false, length = 20)
    public String password;

    @Column(nullable = false, length = 50)
    public String email;

    @Column(nullable = false, length = 11)
    public String phone;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false, columnDefinition = "varchar(30)")
    public AdminGroup adminGroup;

    @Column(columnDefinition = "boolean default true")
    public boolean vaild = true;

    @Override
    public String validate() {
        if (username.isEmpty() || password.isEmpty()) {
            return ERROR_EMPTY;
        }

        if (username.length() < 6 || password.length() < 6) {
            return ERROR_LOGIN;
        }

        return null;
    }
}

