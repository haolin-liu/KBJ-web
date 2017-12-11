package models.entities;

import play.data.validation.Constraints;

import javax.persistence.*;

@Entity
public class MallCategory extends BaseModel {

    @Id
    public Long id;

    @Constraints.Required
    @Constraints.MaxLength(100)
    @Column(length=100)
    public String name;

    @Constraints.Required
    @Constraints.MaxLength(500)
    @Column(length=500)
    public String link;

    @Constraints.Required
    @Constraints.MaxLength(30)
    @Column(length=30)
    public String mall;

    @Constraints.Required
    @Constraints.MaxLength(500)
    @Column(length=500)
    public String tag;

    @Constraints.Required
    @Column(columnDefinition = "boolean default true")
    public Boolean valid;

    @Constraints.Required
    @Column(columnDefinition = "boolean default true")
    public Boolean isCrawlTarget;

    @OneToOne(mappedBy = "mallCategory")
    public CategoryMapping categoryMapping;

}
