package softuni.exam.instagraphlite.models.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@Getter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class UserNameDTO {
    private String username;
}
