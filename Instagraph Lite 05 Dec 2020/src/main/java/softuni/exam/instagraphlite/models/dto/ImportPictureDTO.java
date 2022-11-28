package softuni.exam.instagraphlite.models.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class ImportPictureDTO {

    @NotNull
    private String path;

    @NotNull
    @Min (500)
    @Max (60000)
    private double size;
}
