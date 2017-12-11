package services.manage;

import models.entities.Admin;
import models.entities.LoginAttempt;
import repository.DatabaseExecutionContext;
import repository.AdminRepository;
import repository.LoginAttemptRepository;

import javax.inject.Inject;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class LoginService {

    private final AdminRepository adminRepository;
    private final LoginAttemptRepository loginAttemptRepository;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public LoginService(
            AdminRepository adminRepository,
            LoginAttemptRepository loginAttemptRepository,
            DatabaseExecutionContext executionContext) {
        this.adminRepository = adminRepository;
        this.loginAttemptRepository = loginAttemptRepository;
        this.executionContext = executionContext;
    }

//    public CompletionStage<Admin> login(String username, String password) {
//        return supplyAsync(() -> {
//            return this.adminRepository.login(username, password);
//        } , executionContext);
//    }
    public Admin login(String username, String password) {
        return adminRepository.login(username, password);
    }

    public void addLoginAttempt(LoginAttempt loginAttempt, String account, String ip) {
        if (loginAttempt == null) {
            loginAttempt = new LoginAttempt();
            loginAttempt.accountType = "MANAGER";
            loginAttempt.account = account;
            loginAttempt.ip = ip;
            loginAttempt.total = 1;

            loginAttemptRepository.addLoginAttempt(loginAttempt);
        } else {
            if (loginAttempt.total >= 5) {
                loginAttempt.total = 1;
            } else {
                ++loginAttempt.total;
            }

            loginAttemptRepository.updateLoginAttempt(loginAttempt);
        }
    }

    public void deleteLoginAttempt(LoginAttempt loginAttempt) {
        loginAttempt.total = 0;

        loginAttemptRepository.updateLoginAttempt(loginAttempt);
    }

    public LoginAttempt getLoginAttempt(String account, String ip) {
        return loginAttemptRepository.getLoginAttempt("MANAGER", account, ip);
    }

    public List<LoginAttempt> getLoginAttemptsByIp(String ip) {
        return loginAttemptRepository.getLoginAttemptsByIp(ip);
    }

}
