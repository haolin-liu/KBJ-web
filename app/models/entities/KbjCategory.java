package models.entities;

import play.data.validation.Constraints;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.*;
import java.util.List;

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
    @Column(name = "parent_id")
    public KbjCategory parent;

    // not necessary for CategoryExhibition search.
    @OneToMany(mappedBy = "kbjCategory")
    public List<CategoryExhibition> categoryExhibition;

}
