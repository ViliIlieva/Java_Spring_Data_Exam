package softuni.exam.instagraphlite.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import softuni.exam.instagraphlite.models.entity.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "post")
@XmlAccessorType(XmlAccessType.FIELD)
public class ImportPostDTO {

    @XmlElement
    @NotNull
    @Size(min = 21)
    private String caption;

    @XmlElement
    @NotNull
    private UserNameDTO user;

    @XmlElement
    @NotNull
    private PicturePathDTO picture;
}
