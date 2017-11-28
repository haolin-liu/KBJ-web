package repository;

import io.ebean.Ebean;
import java.util.Date;
import java.util.Map;
import io.ebean.PagedList;
import java.util.Optional;
import javax.inject.Inject;
import io.ebean.Transaction;
import models.entities.User;
import io.ebean.EbeanServer;
import play.db.ebean.EbeanConfig;
import models.form.UserSearchForm;
import play.libs.concurrent.HttpExecutionContext;

public class UserRepository {
    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public UserRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext, HttpExecutionContext httpExecutionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
        this.httpExecutionContext = httpExecutionContext;
    }

    public PagedList<User> find(int page, int pageSize, UserSearchForm userSearchForm) {
        return ebeanServer.find(User.class).where()
            .ilike("name", "%" + userSearchForm.name + "%")
            .ilike("email", "%" + userSearchForm.email + "%")
            .ilike("phone", "%" + userSearchForm.phone + "%")
            .setFirstRow(page * pageSize)
            .setMaxRows(pageSize)
            .findPagedList();
    }

    public Optional<Long> update(Map<String, String[]> data) {
        int count = data.get("seqId").length;
        Optional<Long> value = Optional.empty();
        // 更新
        for (int i = 0; i < count; i++) {
            if ("1".equals(data.get("validChgFlg")[i])) {
                long updateId = Long.parseLong(data.get("seqId")[i]);
                String isValid = data.get("isChk")[i];
                boolean valid = Boolean.parseBoolean(isValid);

                Transaction txn = ebeanServer.beginTransaction();
                try {
                    User user = ebeanServer.find(User.class).setId(updateId).findUnique();
                    if (user != null) {
                        user.valid = valid;
                        user.updateDate = new Date();
                        user.update();
                        txn.commit();
                        value = Optional.of(updateId);
                    }
                } finally {
                    txn.end();
                }
            }
        }
        return value;
    }
}
