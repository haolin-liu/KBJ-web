package models.entities;

import play.data.validation.Constraints;

import javax.persistence.*;

/**
 *  @author yue-yao
 *  @date 2017/11/28
 */
@Entity
public class CategoryMapping extends BaseModel {

    @Id
    public Long id;

    @ManyToOne
    @Constraints.Required
    public KbjCategory kbjCategory;

    @OneToOne
    @Constraints.Required
    public MallCategory mallCategory;

}
