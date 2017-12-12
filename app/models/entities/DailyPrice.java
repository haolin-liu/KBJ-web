package models.entities;

import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.Date;

@Constraints.Validate
@Entity
public class DailyPrice extends BaseModel {

    @Id
    public Long id;

    @ManyToOne
    public KbjCategory kbjCategory;

    @Constraints.Required
    @Constraints.MaxLength(30)
    public String mall;

    @Constraints.Required
    @Constraints.MaxLength(50)
    public String skuid;

    @Constraints.Required
    @Column(columnDefinition="decimal(15,2)")
    public Float price;

    @Column(columnDefinition="decimal(15,2)")
    public Float refPrice;

    @Column(columnDefinition="decimal(15,2)")
    public Float discount;

    @Column(columnDefinition="decimal(15,2)")
    public Float discountRate;

    @Constraints.Required
    @Temporal(TemporalType.DATE)
    public Date date;

    @Constraints.Required
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    public Date timestamp;
}
