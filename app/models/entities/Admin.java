package models.entities;

import play.data.validation.Constraints;

import javax.persistence.*;

@Constraints.Validate
@Entity
public class Admin extends BaseModel implements Constraints.Validatable<String> {

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
            return "用户名或密码不能为空！";
        }

        return null;
    }
}

