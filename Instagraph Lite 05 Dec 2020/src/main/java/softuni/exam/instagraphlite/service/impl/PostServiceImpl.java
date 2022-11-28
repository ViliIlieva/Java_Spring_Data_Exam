package softuni.exam.instagraphlite.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.dto.ImportPostDTO;
import softuni.exam.instagraphlite.models.dto.ImportPostsDTO;
import softuni.exam.instagraphlite.models.entity.Picture;
import softuni.exam.instagraphlite.models.entity.Post;
import softuni.exam.instagraphlite.models.entity.User;
import softuni.exam.instagraphlite.repository.PictureRepository;
import softuni.exam.instagraphlite.repository.PostRepository;
import softuni.exam.instagraphlite.repository.UserRepository;
import softuni.exam.instagraphlite.service.PostService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    Path path = Path.of ("src/main/resources/files/posts.xml");

    private final PostRepository postRepository;
    private final PictureRepository pictureRepository;
    private final UserRepository userRepository;

    private final Unmarshaller unmarshaller;
    private final Validator validator;
    private final ModelMapper modelMapper;

    public PostServiceImpl(PostRepository postRepository, PictureRepository pictureRepository, UserRepository userRepository) throws JAXBException {
        this.postRepository = postRepository;
        this.pictureRepository = pictureRepository;
        this.userRepository = userRepository;

        JAXBContext context = JAXBContext.newInstance (ImportPostsDTO.class);
        this.unmarshaller = context.createUnmarshaller ();
        this.validator = Validation.buildDefaultValidatorFactory ().getValidator ();
        this.modelMapper = new ModelMapper ();
    }

    @Override
    public boolean areImported() {
        return this.postRepository.count () > 0;
    }

    @Override
    public String readFromFileContent() throws IOException {
        return Files.readString (path);
    }

    @Override
    public String importPosts() throws IOException, JAXBException {
        ImportPostsDTO postDTOs = (ImportPostsDTO)
                this.unmarshaller.unmarshal (
                        new FileReader (path.toAbsolutePath ().toFile ()));

        return postDTOs.getPosts ().stream ()
                .map (this::importPost)
                .collect (Collectors.joining ("\n"));
    }

    private String importPost(ImportPostDTO dto) {
        Set<ConstraintViolation<ImportPostDTO>> validate = validator.validate (dto);

        if (!validate.isEmpty ()) {
            return "Invalid Post";
        }

        Optional<Picture> picture = this.pictureRepository.findByPath (dto.getPicture ().getPath ());
        Optional<User> user = this.userRepository.findByUsername (dto.getUser ().getUsername ());

        Post post = this.modelMapper.map (dto, Post.class);

        if (picture.isPresent () && user.isPresent ()) {

            post.setUser (user.get ());
            post.setPictures (picture.get ());

            this.postRepository.save (post);
        }else {
            return "Invalid Post";
        }
        return String.format ("Successfully imported Post, made by %s",post.getUser ().getUsername ());
    }
}
