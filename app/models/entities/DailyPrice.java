package models.entities;

import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.Date;

@Constraints.Validate
@Entity
public class DailyPrice extends BaseModel {

    @Id
    public Long id;

    @Constraints.Required
    @Constraints.MaxLength(30)
    public String mall;

    @Constraints.Required
    @Constraints.MaxLength(50)
    public String skuid;

    @Constraints.Required
    @Column(name = "price", columnDefinition="decimal(15,2)")
    public Float price;

    @Constraints.Required
    @Column(name = "refPrice", columnDefinition="decimal(15,2)")
    public Float refPrice;

    @Constraints.Required
    @Temporal(TemporalType.DATE)
    public Date date;

    @Constraints.Required
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    public Date timestamp;
}
