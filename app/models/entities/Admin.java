package models.entities;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import play.data.validation.Constraints;
import play.db.ebean.EbeanConfig;
import services.manage.LoginService;

import javax.inject.Inject;
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

//    private final Form
    private final LoginService loginService;
    private final EbeanServer ebeanServer;

    @Inject
    public Admin(LoginService loginService, EbeanConfig ebeanConfig) {
        this.loginService = loginService;
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
    }

    @Override
    public String validate() {
        System.out.println("-----------------------------------1");
        System.out.println(loginService);
        System.out.println(ebeanServer);

        if (username.isEmpty() || password.isEmpty()) {
            return "用户名或密码不能为空！";
        }

        return null;
    }
}

