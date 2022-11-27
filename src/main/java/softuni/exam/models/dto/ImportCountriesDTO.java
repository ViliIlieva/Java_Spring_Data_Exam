package softuni.exam.models.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class ImportCountriesDTO {

    @Size(min = 2, max = 60)
    @NotNull

    private String countryName;

    @Size(min = 2, max = 20)
    @NotNull
    private String currency;
}
