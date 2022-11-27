package softuni.exam.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import softuni.exam.models.entity.DayOfWeek;

import javax.persistence.Column;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "forecast")
@XmlAccessorType(XmlAccessType.FIELD)
public class ImportForecastsDTO {

    @XmlElement(name = "day_of_week")
    @NotNull
    private DayOfWeek dayOfWeek;

    @XmlElement(name = "max_temperature")
    @Min (-20)
    @Max (60)
    @NotNull
    private double maxTemperature;

    @XmlElement(name = "min_temperature")
    @Min (-50)
    @Max (40)
    @NotNull
    private double minTemperature;

    @XmlElement
    @NotNull
    private String sunrise;

    @XmlElement
    @NotNull
    private String sunset;

    @XmlElement
    private Long city;
}
