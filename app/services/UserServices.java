package services;

import java.util.Date;
import java.util.Map;
import io.ebean.PagedList;
import javax.inject.Inject;
import models.entities.User;
import models.form.RegisterForm;
import play.data.FormFactory;
import play.mvc.Result;
import repository.UserRepository;
import models.form.UserSearchForm;
import repository.DatabaseExecutionContext;
import java.util.concurrent.CompletionStage;
import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
* This service created to get datas from entity
*  @author lv
*  @date 2017/12/4
*/
public class UserServices {

    private final UserRepository userRepository;
    private final FormFactory formFactory;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public UserServices(UserRepository userRepository, FormFactory formFactory, DatabaseExecutionContext executionContext) {
        this.userRepository = userRepository;
        this.formFactory = formFactory;
        this.executionContext = executionContext;
    }

    /**
     * 取得用户数据
     * @param page
     * @param pageSize
     * @param userSearchForm
     * @return
     */
    public CompletionStage<PagedList<User>> getUsers(int page, int pageSize, UserSearchForm userSearchForm) {
        return supplyAsync(() -> {
            return userRepository.find(page, pageSize, userSearchForm);
        },executionContext);
    }

    /**
     * 通过关键字查找用户
     * @param name
     * @param password
     * @return
     */
    public User getUsers(String name, String password, int loginMode) {
        if (loginMode == 1) {
            return userRepository.findByEmail(name, password);
        } else if( loginMode == 2) {
            return userRepository.findByPhone(name, password);
        } else {
            return userRepository.findByName(name, password);
        }
    }

    /**
     * 通过关键字查找用户
     * @param name
     * @return
     */
    public User getUsers(String name, int loginMode) {
        if (loginMode == 1) {
            return userRepository.findByName(name);
        } else {
            return userRepository.findByEmail(name);
        }
    }

    /**
     * 更新用户数据
     * @param data
     * @return
     */
    public CompletionStage<PagedList<User>> updUser(Map<String, String[]> data) {

        // 取得检索条件
        String name = data.get("searchName")[0];
        String email = data.get("searchEmail")[0];
        String phone = data.get("searchPhone")[0];
        String searchPage = data.get("searchPage")[0];
        int page = Integer.parseInt(searchPage);

        return supplyAsync(() -> {
            return userRepository.update(data);
        }, executionContext).thenApplyAsync((v) -> {
            UserSearchForm userSearchForm = new UserSearchForm(page, name, phone, email, false);
            return userRepository.find(page, 2, userSearchForm);
        });
    }

    /**
     * 插入用户数据
     * @param registerForm
     * @return
     */
  public String save(RegisterForm registerForm) {
      User user = new User();

      user.name = registerForm.name;
      user.email = registerForm.email;
      user.password = registerForm.password;
      user.signinWay = "DIRECT";
      user.createDate = new Date();
      user.createUser = "system";
      user.updateDate = new Date();
      user.updateUser = "system";

      return userRepository.insert(user);
  }
}
