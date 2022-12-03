package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportTaskDTO;
import softuni.exam.models.dto.ImportTasksDTO;
import softuni.exam.models.entity.Car;
import softuni.exam.models.entity.Mechanic;
import softuni.exam.models.entity.Part;
import softuni.exam.models.entity.Task;
import softuni.exam.repository.CarRepository;
import softuni.exam.repository.MechanicRepository;
import softuni.exam.repository.PartRepository;
import softuni.exam.repository.TaskRepository;
import softuni.exam.service.TaskService;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {
    private final Path path = Path.of ("src/main/resources/files/xml/tasks.xml");

    private final TaskRepository taskRepository;
    private final CarRepository carRepository;
    private final MechanicRepository mechanicRepository;
    private final PartRepository partRepository;

    private final Unmarshaller unmarshaller;
    private final Validator validator;
    private final ModelMapper modelMapper;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, CarRepository carRepository, MechanicRepository mechanicRepository, PartRepository partRepository) throws JAXBException {
        this.taskRepository = taskRepository;
        this.carRepository = carRepository;
        this.mechanicRepository = mechanicRepository;
        this.partRepository = partRepository;

        JAXBContext context = JAXBContext.newInstance (ImportTasksDTO.class);
        this.unmarshaller = context.createUnmarshaller ();

        this.validator = Validation.buildDefaultValidatorFactory ().getValidator ();

        this.modelMapper = new ModelMapper ();

        //    "yyyy-MM-dd HH:mm:ss"
        this.modelMapper.addConverter (ctx -> LocalDateTime.parse
                        (ctx.getSource (), DateTimeFormatter.ofPattern ("yyyy-MM-dd HH:mm:ss")),
                String.class, LocalDateTime.class);
    }

    @Override
    public boolean areImported() {
        return this.taskRepository.count () > 0;
    }

    @Override
    public String readTasksFileContent() throws IOException {
        return Files.readString (path);
    }

    @Override
    public String importTasks() throws IOException, JAXBException {
        ImportTasksDTO tasksDTOs = (ImportTasksDTO)
                this.unmarshaller.unmarshal (
                        new FileReader (path.toAbsolutePath ().toString ()));

        return tasksDTOs.getTask ()
                .stream ()
                .map (this::importTask)
                .collect (Collectors.joining ("\n"));
    }

    private String importTask(ImportTaskDTO dto) {
        Set<ConstraintViolation<ImportTaskDTO>> validate = validator.validate (dto);

        if (!validate.isEmpty ()) {
            return "Invalid task";
        }

        Mechanic mechanic = this.mechanicRepository.findByFirstNameOrderByFirstName (dto.getMechanic ().getFirstName ());
        Car car = this.carRepository.findByIdOrderById (dto.getCar ().getId ());
        Part part = this.partRepository.findByIdOrderById (dto.getPart ().getId ());

        if(mechanic != null & car != null & part != null){
            Task task = this.modelMapper.map (dto, Task.class);

            task.setMechanic (mechanic);
            task.setCar (car);
            task.setPart (part);

            this.taskRepository.save (task);

            return String.format ("Successfully imported task %.2f", task.getPrice ());

        }else {
            return "Invalid task";
        }
    }

    @Override
    public String getCoupeCarTasksOrderByPrice() {
        List<Task> onlyCoupeCars = this.taskRepository.findCoupeCars ();

        return onlyCoupeCars
                .stream ()
                .map (Task::toString)
                .collect(Collectors.joining("\n"));
    }
}
