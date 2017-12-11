package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.entities.Admin;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;

public class AdminRepository {

    private final EbeanServer ebeanServer;

    @Inject
    public AdminRepository(EbeanConfig ebeanConfig) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
    }

    public Admin login(String username, String password) {
        return ebeanServer.find(Admin.class).where()
                .eq("username", username)
                .eq("password", password)
                .findUnique();
    }

}
