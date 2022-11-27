package exam.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exam.model.dto.ImportCustomerDTO;
import exam.model.dto.ImportLaptopDTO;
import exam.model.entity.Laptop;
import exam.model.entity.Shop;
import exam.repository.LaptopRepository;
import exam.repository.ShopRepository;
import exam.service.LaptopService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LaptopServiceImpl implements LaptopService {
    private final LaptopRepository laptopRepository;
    private final ShopRepository shopRepository;

    private final Gson gson;
    private final Validator validator;
    private final ModelMapper modelMapper;

    @Autowired
    public LaptopServiceImpl(LaptopRepository laptopRepository, ShopRepository shopRepository) {
        this.laptopRepository = laptopRepository;
        this.shopRepository = shopRepository;

        this.gson = new GsonBuilder ().create ();
        this.validator = Validation.buildDefaultValidatorFactory ().getValidator ();
        this.modelMapper = new ModelMapper ();
    }

    @Override
    public boolean areImported() {
        return this.laptopRepository.count () > 0;
    }

    @Override
    public String readLaptopsFileContent() throws IOException {
        Path path = Path.of ("src/main/resources/files/json/laptops.json");
        return Files.readString (path);
    }

    @Override
    public String importLaptops() throws IOException {
        String json = this.readLaptopsFileContent ();

        ImportLaptopDTO[] laptopDTOs = this.gson.fromJson (json, ImportLaptopDTO[].class);

        return Arrays.stream (laptopDTOs)
                .map (this::importLaptop)
                .collect(Collectors.joining ("\n"));
    }

    private String importLaptop(ImportLaptopDTO dto) {
        Set<ConstraintViolation<ImportLaptopDTO>> validate = validator.validate (dto);

        if(!validate.isEmpty ()){
            return "Invalid Laptop";
        }

        if(this.laptopRepository.findByMacAddress (dto.getMacAddress ()).isPresent ()){
            return "Invalid Laptop";
        }

        Laptop laptop = this.modelMapper.map (dto, Laptop.class);
        Optional<Shop> shop = this.shopRepository.findByName (dto.getShop ().getName ());
        laptop.setShop (shop.get ());

        laptopRepository.save (laptop);

        return String.format ("Successfully imported Customer %s - %.2f - %s - %s",
                laptop.getMacAddress (), laptop.getCpuSpeed (),
                laptop.getRam (), laptop.getStorage ());
    }

    @Override
    public String exportBestLaptops() {
        List<Laptop> laptops = this.laptopRepository.findByOrderByCpuSpeedDescRamDescStorageDescMacAddress ();

        return laptops.stream ()
                .map (Laptop::toString)
                .collect(Collectors.joining("\n"));
    }
}
