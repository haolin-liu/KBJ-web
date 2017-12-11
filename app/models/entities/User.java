package models.entities;

import play.data.validation.Constraints;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *  @author lv
 *  @date 2017/11/27
 */
@Entity
public class User extends BaseModel {

    @Id
    public Long seqId;

    @Column(length = 20)
    @Constraints.MaxLength(20)
    public String id;

    @Column(length = 30)
    @Constraints.MaxLength(30)
    public String name;

    @Column(length = 20)
    @Constraints.MaxLength(20)
    public String password;

    @Column(length = 255)
    @Constraints.MaxLength(255)
    public String icon;

    @Column(length = 50)
    @Constraints.MaxLength(50)
    public String email;

    @Column(length = 11)
    @Constraints.MaxLength(11)
    public String phone;

    @Constraints.Required
    @Constraints.MaxLength(20)
    @Column(columnDefinition =  "varchar(20) DEFAULT 'DIRECT'")
    public String signinWay;

    @Column(length = 30)
    @Constraints.MaxLength(30)
    public String idOfsigninWay;

    @Constraints.Required
    @Column(columnDefinition = "boolean default false")
    public boolean valid;
}
