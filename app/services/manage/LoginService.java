package services.manage;

import models.entities.Admin;
import repository.DatabaseExecutionContext;
import repository.AdminRepository;

import javax.inject.Inject;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class LoginService {

    private final AdminRepository adminRepository;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public LoginService(AdminRepository adminRepository, DatabaseExecutionContext executionContext) {
        this.adminRepository = adminRepository;
        this.executionContext = executionContext;
    }

//    public CompletionStage<Admin> login(String username, String password) {
//        return supplyAsync(() -> {
//            return this.adminRepository.login(username, password);
//        } , executionContext);
//    }
    public Admin login(String username, String password) {
        return this.adminRepository.login(username, password);
    }

}
