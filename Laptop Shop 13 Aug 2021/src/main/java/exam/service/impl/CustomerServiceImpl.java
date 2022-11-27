package exam.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exam.model.dto.ImportCustomerDTO;
import exam.model.entity.Customer;
import exam.model.entity.Town;
import exam.repository.CustomerRepository;
import exam.repository.TownRepository;
import exam.service.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final TownRepository townRepository;

    private final Gson gson;
    private final Validator validator;
    private final ModelMapper modelMapper;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, TownRepository townRepository) {
        this.customerRepository = customerRepository;
        this.townRepository = townRepository;

        this.gson = new GsonBuilder ().create ();
        this.validator = Validation.buildDefaultValidatorFactory ().getValidator ();
        this.modelMapper = new ModelMapper ();

        this.modelMapper.addConverter (ctx -> LocalDate.parse (ctx.getSource (), DateTimeFormatter.ofPattern ("dd/MM/yyyy")),
                String.class, LocalDate.class);
    }

    @Override
    public boolean areImported() {
        return this.customerRepository.count () > 0;
    }

    @Override
    public String readCustomersFileContent() throws IOException {
        Path path = Path.of ("src/main/resources/files/json/customers.json");
        return Files.readString (path);
    }

    @Override
    public String importCustomers() throws IOException {
        String json = this.readCustomersFileContent ();

        ImportCustomerDTO[] customerDTOs = this.gson.fromJson (json, ImportCustomerDTO[].class);

        return Arrays.stream (customerDTOs)
                .map (this::importCustomer)
                .collect(Collectors.joining ("\n"));
    }

    private String importCustomer(ImportCustomerDTO dto) {
        Set<ConstraintViolation<ImportCustomerDTO>> validate = validator.validate (dto);

        if(!validate.isEmpty ()){
            return "Invalid Customer";
        }
        if(this.customerRepository.findByEmail (dto.getEmail ()).isPresent ()){
            return "Invalid Customer";
        }

        Customer customer = this.modelMapper.map (dto, Customer.class);
        Optional<Town> town = this.townRepository.findByName (dto.getTown ().getName ());
        customer.setTown (town.get ());

        customerRepository.save (customer);

        return String.format ("Successfully imported Customer %s %s - %s",
                customer.getFirstName (), customer.getLastName (), customer.getEmail ());

    }
}
