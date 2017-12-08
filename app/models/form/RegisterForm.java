package models.form;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.data.validation.Constraints.Validatable;

/**
 *  @author 吕
 *  @date  2017/12/8
 */
@Constraints.Validate
public class RegisterForm implements Validatable<List<ValidationError>> {

    public String name;

    public String email;

    public String password;

    public String confirmPassword;

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> result = new ArrayList<ValidationError>();

        if (name == null || name.trim().length() == 0) {
            result.add(new ValidationError("name", "请输入用户名"));
        } else {
            if (name.trim().length() > 30) {
                result.add(new ValidationError("name", "用户名长度不能超过30位"));
            }
        }

        if (email == null || email.trim().length() == 0) {
            result.add(new ValidationError("email", "请输入邮箱"));
        } else {
            if (email.trim().length() > 50) {
                result.add(new ValidationError("email", "邮箱长度不能超过50位"));
            } else {
                if (!isEamil(email)) {
                    result.add(new ValidationError("email", "邮箱格式不正确"));
                }
            }
        }

        if (password == null || password.trim().length() == 0) {
            result.add(new ValidationError("password", "请输入密码"));
        } else {
            if (!isPassword(password)) {
               result.add(new ValidationError("password", "密码只能由数字，字母，特殊字符(!.#@$%^&*)任意两个组成，长度为8到20位"));
            }
        }

        if (confirmPassword == null || confirmPassword.trim().length() == 0) {
            result.add(new ValidationError("confirmPassword", "请输入确认密码"));
        }

        if (!password.equals(confirmPassword)) {
            result.add(new ValidationError("confirmPassword", "输入的密码不一致"));
        }

        return result.isEmpty() ? null : result;
    }

    public boolean isEamil(String email) {
        Pattern em = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
        Matcher mt = em.matcher(email);

        if (mt.matches()) {
            return true;
        }

        return false;
    }

    public boolean isPassword(String password) {
        Pattern pwd = Pattern.compile("^(?![\\d]+$)(?![a-zA-Z]+$)(?![!.#@$%^&*]+$)[\\da-zA-Z!.#@$%^&*]{8,20}$");
        Matcher mt = pwd.matcher(password);

        if (mt.matches()) {
            return true;
        }

        return false;
    }
}
