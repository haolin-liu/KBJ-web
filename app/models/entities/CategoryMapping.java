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
    @Column(name = "kbj_cate_id")
    @Constraints.Required
    public KbjCategory kbjCategory;

    @OneToOne
    @Constraints.Required
    @Column(name = "mall_cate_id")
    public MallCategory mallCategory;

}
