package softuni.exam.instagraphlite.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.dto.ImportPictureDTO;
import softuni.exam.instagraphlite.models.entity.Picture;
import softuni.exam.instagraphlite.repository.PictureRepository;
import softuni.exam.instagraphlite.service.PictureService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PictureServiceImpl implements PictureService {
    private  final PictureRepository pictureRepository;

    private final Gson gson;
    private final Validator validator;
    private final ModelMapper modelMapper;

    @Autowired
    public PictureServiceImpl(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;

        this.gson = new GsonBuilder ().create ();
        this.validator = Validation.buildDefaultValidatorFactory ().getValidator ();
        this.modelMapper = new ModelMapper ();
    }

    @Override
    public boolean areImported() {
        return this.pictureRepository.count () >0;
    }

    @Override
    public String readFromFileContent() throws IOException {
        Path path = Path.of ("src/main/resources/files/pictures.json");
        return Files.readString (path);
    }

    @Override
    public String importPictures() throws IOException {
        String json = readFromFileContent ();

        ImportPictureDTO[] pictureDTOs = this.gson.fromJson (json, ImportPictureDTO[].class);

        return Arrays.stream (pictureDTOs)
                .map (this::importPicture)
                .collect(Collectors.joining ("\n"));
    }

    private String importPicture(ImportPictureDTO dto) {
        Set<ConstraintViolation<ImportPictureDTO>> validate = validator.validate (dto);

        if (!validate.isEmpty ()){
            return "Invalid Picture";
        }
        if(this.pictureRepository.findByPath (dto.getPath ()).isPresent ()){
            return "Invalid Picture";
        }
        Picture pictures = this.modelMapper.map (dto, Picture.class);
        this.pictureRepository.save (pictures);

        return (String.format
                ("Successfully imported Picture, with size %.2f",
                        pictures.getSize ()));
    }

    @Override
    public String exportPictures() {
        double size = 30000;

        return this.pictureRepository.findAllBySizeGreaterThanOrderBySizeAsc (size)
                .stream ().map (Picture::toString)
                .collect(Collectors.joining("\n"));
    }
}
