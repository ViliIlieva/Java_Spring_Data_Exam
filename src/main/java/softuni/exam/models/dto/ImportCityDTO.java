package softuni.exam.models.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import softuni.exam.models.entity.Country;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class ImportCityDTO {

    @Size(min = 2, max = 60)
    @NotNull
    private String cityName;

    @Size(min = 2)
    private String description;

    @Min (500)
    @NotNull
    private int population;

    private Long country;
}
