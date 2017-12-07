package models.entities;

import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *  @author jie-z
 *  @date 2017/12/07
 */
@Entity
public class CategoryExhibition extends BaseModel {

    @Id
    public Long id;

    @Constraints.Required
    @Column(nullable = false, name = "kbj_category_id")
    public KbjCategory kbjCategory;

    @Constraints.Required
    @Column(nullable = false, columnDefinition = "default 1")
    public Integer priority;

    @Constraints.Required
    @Column(columnDefinition = "boolean default true")
    public Boolean valid;

}
