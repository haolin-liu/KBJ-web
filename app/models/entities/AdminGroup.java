package models.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 管理者组
 *
 * @author lichen
 * @date 2017.12.7
 */
@Entity
public class AdminGroup extends BaseModel {

    @Id @Column(length = 30)
    public String groupId;

    @Column(nullable = false, length = 40)
    public String groupName;

    public String description;

    @Column(columnDefinition = "boolean default true")
    public boolean vaild = true;

}
