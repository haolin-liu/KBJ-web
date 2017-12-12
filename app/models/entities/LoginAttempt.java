package models.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 用户登录尝试记录
 *
 * @author lichen
 * @date 2017.12.7
 */
@Table(
    name="login_attempt",
    indexes = { @Index(name = "ix_login_attempt", columnList = "account_type, account, ip, vaild") }
)
@Entity
public class LoginAttempt extends BaseModel {

    @Id
    public long seqId;

    // CUSTOMER/MANAGER
    @NotNull @Size(max = 20)
    public String accountType;

    @NotNull @Size(max = 50)
    public String account;

    @NotNull @Size(max = 30)
    public String ip;

    @NotNull @Size(max = 2)
    public int total;

    @Column(columnDefinition = "boolean default true")
    public boolean vaild = true;

}
