package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Transaction;
import models.entities.LoginAttempt;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.List;

public class LoginAttemptRepository {

    private final EbeanServer ebeanServer;

    @Inject
    public LoginAttemptRepository(EbeanConfig ebeanConfig) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
    }

    public void addLoginAttempt(LoginAttempt loginAttempt) {
        ebeanServer.insert(loginAttempt);
    }

    public void updateLoginAttempt(LoginAttempt loginAttempt) {
        Transaction txn = ebeanServer.beginTransaction();

        try {
            loginAttempt.update();
            txn.commit();
        } finally {
            txn.end();
        }
    }

    public LoginAttempt getLoginAttempt(String accountType, String account, String ip) {
        return ebeanServer.find(LoginAttempt.class).where()
                .eq("accountType", accountType)
                .eq("account", account)
                .eq("ip", ip)
                .eq("vaild", true)
                .findUnique();
    }

    public List<LoginAttempt> getLoginAttemptsByIp(String ip) {
        return ebeanServer.find(LoginAttempt.class).where()
                .eq("ip", ip)
                .eq("vaild", true)
                .findList();
    }
}
