package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.entities.Admin;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;

/**
 * @author lichen
 * @date 2017.12.7
 */
public class AdminRepo {

    private final EbeanServer ebeanServer;

    @Inject
    public AdminRepo(EbeanConfig ebeanConfig) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
    }

    public Admin login(String username, String password) {
        return ebeanServer.find(Admin.class)
                .fetch("adminGroup", "groupId")
                .where()
                .eq("username", username)
                .eq("password", password)
                .eq("vaild", true)
                .findUnique();
    }

}
