package softuni.exam.models.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import softuni.exam.models.entity.Town;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class ImportAgentDTO {

    @NotNull
    @Size(min = 2)
    private String firstName;

    @NotNull
    @Size(min = 2)
    private String lastName;

    @NotNull
    private String town;

    @NotNull
    @Email
    private String email;


}
