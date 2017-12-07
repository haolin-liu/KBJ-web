package models.entities;

import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Constraints.Validate
@Entity
public class Admin extends BaseModel implements Constraints.Validatable<String> {

    @Id
    public Long id;

    @Column(nullable = false, length = 30)
    public String username;

    @Column(nullable = false, length = 100)
    public String password;

    @Column(nullable = false, length = 15)
    public String userGroupId;

    @Override
    public String validate() {
        if (username.isEmpty() || password.isEmpty()) {
            return "用户名或密码不能为空！";
        }

        return null;
    }
}

