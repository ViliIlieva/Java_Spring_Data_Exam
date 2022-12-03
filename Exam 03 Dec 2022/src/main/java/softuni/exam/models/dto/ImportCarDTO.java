package softuni.exam.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import softuni.exam.models.entity.CarType;

import javax.persistence.Enumerated;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "car")
@XmlAccessorType(XmlAccessType.FIELD)
public class ImportCarDTO {

    @NotNull
    @Size(min = 2, max = 30)
    private String carMake;

    @NotNull
    @Size(min = 2, max = 30)
    private String carModel;

    @NotNull
    @Positive
    private int year;

    @NotNull
    @Size(min = 2, max = 30)
    private String plateNumber;

    @NotNull
    @Positive
    private int kilometers;

    @NotNull
    @Min (1)
    private Double engine;

    @NotNull
    @Enumerated
    private CarType carType;
}
