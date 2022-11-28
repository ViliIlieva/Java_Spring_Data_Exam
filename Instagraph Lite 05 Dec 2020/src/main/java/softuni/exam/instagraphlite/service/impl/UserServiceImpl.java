package softuni.exam.instagraphlite.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.dto.ImportUserDTO;
import softuni.exam.instagraphlite.models.entity.User;
import softuni.exam.instagraphlite.repository.PictureRepository;
import softuni.exam.instagraphlite.repository.UserRepository;
import softuni.exam.instagraphlite.service.UserService;

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
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PictureRepository pictureRepository;

    private final Gson gson;
    private final Validator validator;
    private final ModelMapper modelMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PictureRepository pictureRepository) {
        this.userRepository = userRepository;
        this.pictureRepository = pictureRepository;

        this.gson = new GsonBuilder ().create ();
        this.validator = Validation.buildDefaultValidatorFactory ().getValidator ();
        this.modelMapper = new ModelMapper ();
    }

    @Override
    public boolean areImported() {
        return this.userRepository.count () >0;
    }

    @Override
    public String readFromFileContent() throws IOException {
        Path path = Path.of ("src/main/resources/files/users.json");
        return Files.readString (path);
    }

    @Override
    public String importUsers() throws IOException {
        String json = readFromFileContent ();

        ImportUserDTO[] userDTOs = this.gson.fromJson (json, ImportUserDTO[].class);

        return Arrays.stream (userDTOs)
                .map (this::importUser)
                .collect(Collectors.joining ("\n"));
    }

    private String importUser(ImportUserDTO dto) {
        Set<ConstraintViolation<ImportUserDTO>> validate = validator.validate (dto);

        if (!validate.isEmpty ()){
            return "Invalid User";
        }
        User user = this.modelMapper.map (dto, User.class);
        if(this.pictureRepository.findByPath (dto.getProfilePicture ()).isEmpty ()){
            return "Invalid User";
        }
        user.setPictures (this.pictureRepository.findByPath (dto.getProfilePicture ()).get ());
        this.userRepository.save (user);
        return (String.format
                ("Successfully imported User: %s",
                        user.getUsername ()));

    }

    @Override
    public String exportUsersWithTheirPosts() {
        return this.userRepository.findAllUsers ()
                .stream ()
                .map (User::toString)
                .collect(Collectors.joining("\n"));
    }
}
















