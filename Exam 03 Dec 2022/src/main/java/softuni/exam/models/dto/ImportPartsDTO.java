package softuni.exam.models.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Getter
@NoArgsConstructor
public class ImportPartsDTO {

    @NotNull
    @Size(min = 2, max = 19)
    private String partName;

    @NotNull
    @Min (10)
    @Max (2000)
    private double price;

    @NotNull
    @Positive
    private int quantity;
}
