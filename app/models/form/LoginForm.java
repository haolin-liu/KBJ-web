package models.form;

import java.util.List;
import java.util.ArrayList;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.data.validation.Constraints.Validatable;

@Constraints.Validate
public class LoginForm implements Validatable<List<ValidationError>> {

    public String name;

    public String password;

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> result = new ArrayList<ValidationError>();

        if (name == null || name.trim().length() == 0) {
            result.add(new ValidationError("name", "请输入用户名/邮箱/手机号"));
        }

        if (password == null || password.trim().length() == 0) {
            result.add(new ValidationError("password", "请输入密码"));
        }

        return result.isEmpty() ? null : result;
    }
}
