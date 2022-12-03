package softuni.exam.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import softuni.exam.models.entity.Car;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "task")
@XmlAccessorType(XmlAccessType.FIELD)
public class ImportTaskDTO {

    @NotNull
    @XmlElement
    private String date;

    @NotNull
    @Positive
    @XmlElement
    private BigDecimal price;

    @XmlElement(name = "car")
    private CarIdDTO car;

    @XmlElement(name = "mechanic")
    private MechanicFirstNameDTO mechanic;

    @XmlElement(name = "part")
    private PartIdDTO part;


}
