package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportCarDTO;
import softuni.exam.models.dto.ImportCarsDTO;
import softuni.exam.models.entity.Car;
import softuni.exam.repository.CarRepository;
import softuni.exam.service.CarService;

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
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CarServiceImpl implements CarService {
    private final Path path = Path.of ("src/main/resources/files/xml/cars.xml");

    private final CarRepository carRepository;

    private final Unmarshaller unmarshaller;
    private final Validator validator;
    private final ModelMapper modelMapper;

    @Autowired
    public CarServiceImpl(CarRepository carRepository) throws JAXBException {
        this.carRepository = carRepository;

        JAXBContext context = JAXBContext.newInstance (ImportCarsDTO.class);
        this.unmarshaller = context.createUnmarshaller ();

        this.validator = Validation.buildDefaultValidatorFactory ().getValidator ();

        this.modelMapper = new ModelMapper ();
    }

    @Override
    public boolean areImported() {
        return this.carRepository.count () > 0;
    }

    @Override
    public String readCarsFromFile() throws IOException {
        return Files.readString (path);
    }

    @Override
    public String importCars() throws IOException, JAXBException {
        ImportCarsDTO carDTOs = (ImportCarsDTO)
                this.unmarshaller.unmarshal (
                        new FileReader (path.toAbsolutePath ().toString ()));

        return carDTOs.getCars ()
                .stream ()
                .map (this::importCar)
                .collect (Collectors.joining ("\n"));
    }

    private String importCar(ImportCarDTO dto) {
        Set<ConstraintViolation<ImportCarDTO>> validate = validator.validate (dto);

        if (!validate.isEmpty ()) {
            return "Invalid car";
        }

        if(this.carRepository.findByPlateNumber (dto.getPlateNumber ()).isPresent ()){
            return "Invalid car";
        }

        Car car = this.modelMapper.map (dto, Car.class);

        this.carRepository.save (car);

        return  String.format ("Successfully imported car %s - %s",
                car.getCarMake (), car.getCarModel ());





    }
}
