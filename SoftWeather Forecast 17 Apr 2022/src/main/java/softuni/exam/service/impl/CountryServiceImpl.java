package softuni.exam.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportCountriesDTO;
import softuni.exam.models.entity.Country;
import softuni.exam.repository.CountryRepository;
import softuni.exam.service.CountryService;

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
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;

    private final Gson gson;
    private final Validator validator;
    private final ModelMapper modelMapper;

    @Autowired
    public CountryServiceImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;

        this.gson = new GsonBuilder ().create ();
        this.validator = Validation.buildDefaultValidatorFactory ().getValidator ();
        this.modelMapper = new ModelMapper ();
    }

    @Override
    public boolean areImported() {
        return this.countryRepository.count () > 0;
    }

    @Override
    public String readCountriesFromFile() throws IOException {
        Path path = Path.of ("src/main/resources/files/json/countries.json");
        return Files.readString (path);
    }

    @Override
    public String importCountries() throws IOException {
        String json = readCountriesFromFile ();

        ImportCountriesDTO [] countriesDTOs = this.gson.fromJson (json, ImportCountriesDTO[].class);
        return Arrays.stream (countriesDTOs)
                .map (this::importCountry)
                .collect (Collectors.joining ("\n"));
    }

    private String importCountry(ImportCountriesDTO dto) {

        Set<ConstraintViolation<ImportCountriesDTO>> validate = this.validator.validate (dto);
        if(!validate.isEmpty ()){
            return "Invalid country";
        }

        if(this.countryRepository.findFirstByCountryName(dto.getCountryName ()).isPresent ()){
            return "Invalid country";
        }

        Country country = this.modelMapper.map (dto, Country.class);
        this.countryRepository.save (country);

        return String.format ("Successfully imported country %s - %s",
                country.getCountryName (), country.getCurrency ());
    }
}
