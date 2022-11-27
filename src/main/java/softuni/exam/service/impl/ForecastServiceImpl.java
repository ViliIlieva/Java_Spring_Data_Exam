package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportForecastsDTO;
import softuni.exam.models.dto.ImportForecastsWrapperDTO;
import softuni.exam.models.entity.City;
import softuni.exam.models.entity.DayOfWeek;
import softuni.exam.models.entity.Forecast;
import softuni.exam.repository.CityRepository;
import softuni.exam.repository.ForecastRepository;
import softuni.exam.service.ForecastService;

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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ForecastServiceImpl implements ForecastService {
    private final Path path = Path.of ("src/main/resources/files/xml/forecasts.xml");

    private final ForecastRepository forecastRepository;
    private final CityRepository cityRepository;

    private final Unmarshaller unmarshaller;
    private final Validator validator;
    private final ModelMapper modelMapper;



    @Autowired
    public ForecastServiceImpl(ForecastRepository forecastRepository, CityRepository cityRepository) throws JAXBException {
        this.forecastRepository = forecastRepository;
        this.cityRepository = cityRepository;

        JAXBContext context = JAXBContext.newInstance (ImportForecastsWrapperDTO.class);
        this.unmarshaller = context.createUnmarshaller ();

        this.validator = Validation
                .buildDefaultValidatorFactory ()
                .getValidator ();

        this.modelMapper = new ModelMapper ();

        this.modelMapper.addConverter (ctx -> LocalTime.parse (ctx.getSource (), DateTimeFormatter.ofPattern ("HH:mm:ss")),
                String.class, LocalTime.class);
    }

    @Override
    public boolean areImported() {
        return this.forecastRepository.count () > 0;
    }

    @Override
    public String readForecastsFromFile() throws IOException {
        return Files.readString (path);
    }

    @Override
    public String importForecasts() throws IOException, JAXBException {
        ImportForecastsWrapperDTO forecastsDTOs = (ImportForecastsWrapperDTO)
                this.unmarshaller.unmarshal (
                new FileReader (path.toAbsolutePath ().toFile ()));

        return forecastsDTOs.getForecasts ().stream ()
                .map (this::importForecast)
                .collect(Collectors.joining ("\n"));
    }

    private String importForecast(ImportForecastsDTO dto) {
        Set<ConstraintViolation<ImportForecastsDTO>> validate = validator.validate (dto);

        if(!validate.isEmpty ()){
            return "Invalid forecasts";
        }

        if(this.cityRepository.findFirstById (dto.getCity ()).isPresent ()) {
            City city = this.cityRepository.findFirstById (dto.getCity ()).get ();
            Optional<Forecast> optForecast = this.forecastRepository.findByDayOfWeekAndCity_CityName (
                    dto.getDayOfWeek (), city.getCityName ());
            if (optForecast.isPresent ()) {
                return "Invalid forecasts";
            }
        }
        Forecast forecast = this.modelMapper.map (dto, Forecast.class);
        forecast.setCity (this.cityRepository.findById (dto.getCity ()).get ());

        this.forecastRepository.save (forecast);

        return String.format ("Successfully import forecast %s - %.2f",
                forecast.getDayOfWeek (), forecast.getMaxTemperature ());
    }

    @Override
    public String exportForecasts() {
        int num = 150000;

        List<Forecast> forecasts = this.forecastRepository
                .findAllByDayOfWeekAndCity_PopulationLessThanOrderByMaxTemperatureDescIdAsc (
                DayOfWeek.SUNDAY, num);
        return forecasts.stream ()
                .map (Forecast::toString)
                .collect(Collectors.joining("\n"));
    }
}
