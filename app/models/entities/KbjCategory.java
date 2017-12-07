package models.entities;

import play.data.validation.Constraints;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.*;

/**
 * KeySearch entity created by Dai
 */

@Constraints.Validate
@Entity
public class KbjCategory extends BaseModel {

    @Id
    public Long id;

    @Constraints.Required
    @Constraints.MaxLength(100)
    @Column(nullable = false, length=100)
    public String name;

    @Constraints.Required
    @Column(columnDefinition = "boolean default true")
    public Boolean isCrawleTarget;

    @Constraints.Required
    @Column(columnDefinition = "boolean default true")
    public Boolean valid;

    @ManyToOne
    @Column(name = "parent_id default 0")
    public KbjCategory parent;

}
