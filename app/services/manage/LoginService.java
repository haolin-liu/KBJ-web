package services.manage;

import models.entities.Admin;
import models.entities.LoginAttempt;
import repository.AdminRepo;
import repository.DatabaseExecutionContext;
import repository.LoginAttemptRepo;

import javax.inject.Inject;

import java.util.List;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * @author lichen
 * @date 2017.12.7
 */
public class LoginService {

    private final AdminRepo adminRepo;
    private final LoginAttemptRepo loginAttemptRepo;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public LoginService(
            AdminRepo adminRepo,
            LoginAttemptRepo loginAttemptRepo,
            DatabaseExecutionContext executionContext) {
        this.adminRepo = adminRepo;
        this.loginAttemptRepo = loginAttemptRepo;
        this.executionContext = executionContext;
    }

    public Admin login(String username, String password) {
        return adminRepo.login(username, password);
    }

    public void addLoginAttempt(LoginAttempt loginAttempt, String account, String ip) {
        if (loginAttempt == null) {
            loginAttempt = new LoginAttempt();
            loginAttempt.accountType = "MANAGER";
            loginAttempt.account = account;
            loginAttempt.ip = ip;
            loginAttempt.total = 1;

            loginAttemptRepo.addLoginAttempt(loginAttempt);
        } else {
            if (loginAttempt.total >= 5) {
                loginAttempt.total = 1;
            } else {
                ++loginAttempt.total;
            }

            loginAttemptRepo.updateLoginAttempt(loginAttempt);
        }
    }

    public void deleteLoginAttempt(LoginAttempt loginAttempt) {
        loginAttempt.total = 0;

        loginAttemptRepo.updateLoginAttempt(loginAttempt);
    }

    public LoginAttempt getLoginAttempt(String account, String ip) {
        return loginAttemptRepo.getLoginAttempt("MANAGER", account, ip);
    }

    public List<LoginAttempt> getLoginAttemptsByIp(String ip) {
        return loginAttemptRepo.getLoginAttemptsByIp(ip);
    }

}
