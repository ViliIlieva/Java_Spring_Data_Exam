package softuni.exam.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportMechanicsDTO;
import softuni.exam.models.dto.ImportPartsDTO;
import softuni.exam.models.entity.Mechanic;
import softuni.exam.repository.MechanicRepository;
import softuni.exam.service.MechanicService;

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
public class MechanicServiceImpl implements MechanicService {
    private final MechanicRepository mechanicRepository;

    private final Gson gson;
    private final Validator validator;
    private final ModelMapper modelMapper;

    @Autowired
    public MechanicServiceImpl(MechanicRepository mechanicRepository) {
        this.mechanicRepository = mechanicRepository;

        this.gson = new GsonBuilder ().create ();
        this.validator = Validation.buildDefaultValidatorFactory ().getValidator ();
        this.modelMapper = new ModelMapper ();
    }

    @Override
    public boolean areImported() {
        return this.mechanicRepository.count () > 0;
    }

    @Override
    public String readMechanicsFromFile() throws IOException {
        Path path = Path.of ("src/main/resources/files/json/mechanics.json");
        return Files.readString (path);
    }

    @Override
    public String importMechanics() throws IOException {
        String json = readMechanicsFromFile ();

        ImportMechanicsDTO[] mechanicDTOs = this.gson.fromJson (json, ImportMechanicsDTO[].class);

        return Arrays.stream (mechanicDTOs)
                .map (this::importMechanic)
                .collect(Collectors.joining ("\n"));
    }

    private String importMechanic(ImportMechanicsDTO dto) {
        Set<ConstraintViolation<ImportMechanicsDTO>> validate = validator.validate (dto);

        if (!validate.isEmpty ()){
            return "Invalid mechanic";
        }
        if(this.mechanicRepository.findByEmail (dto.getEmail ()).isPresent ()){
            return "Invalid mechanic";
        }

        if(this.mechanicRepository.findByFirstName (dto.getFirstName ()).isPresent ()){
            return "Invalid mechanic";
        }

        if(this.mechanicRepository.findByPhone (dto.getPhone ()).isPresent ()){
            return "Invalid mechanic";
        }

        Mechanic mechanic = this.modelMapper.map (dto, Mechanic.class);
        this.mechanicRepository.save (mechanic);

        return (String.format
                ("Successfully imported mechanic %s %s",
                        mechanic.getFirstName (), mechanic.getLastName ()));
    }
}
