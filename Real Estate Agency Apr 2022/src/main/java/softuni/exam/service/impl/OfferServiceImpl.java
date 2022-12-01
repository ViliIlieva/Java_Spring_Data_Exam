package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportOfferDTO;
import softuni.exam.models.dto.ImportOffersDTO;
import softuni.exam.models.entity.Agent;
import softuni.exam.models.entity.Apartment;
import softuni.exam.models.entity.Offer;
import softuni.exam.repository.AgentRepository;
import softuni.exam.repository.ApartmentRepository;
import softuni.exam.repository.OfferRepository;
import softuni.exam.repository.TownRepository;
import softuni.exam.service.OfferService;

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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OfferServiceImpl implements OfferService {
    private final Path path = Path.of ("src/main/resources/files/xml/offers.xml");

    private final OfferRepository offerRepository;
    private  final AgentRepository agentRepository;
    private final ApartmentRepository apartmentRepository;

    private final Unmarshaller unmarshaller;
    private final Validator validator;
    private final ModelMapper modelMapper;

    @Autowired
    public OfferServiceImpl(OfferRepository offerRepository, AgentRepository agentRepository, TownRepository townRepository, ApartmentRepository apartmentRepository) throws JAXBException {
        this.offerRepository = offerRepository;
        this.agentRepository = agentRepository;
        this.apartmentRepository = apartmentRepository;

        JAXBContext context = JAXBContext.newInstance (ImportOffersDTO.class);
        this.unmarshaller = context.createUnmarshaller ();

        this.validator = Validation
                .buildDefaultValidatorFactory ()
                .getValidator ();

        this.modelMapper = new ModelMapper ();

        this.modelMapper.addConverter (ctx -> LocalDate.parse (ctx.getSource (), DateTimeFormatter.ofPattern ("dd/MM/yyyy")),
                String.class, LocalDate.class);
    }

    @Override
    public boolean areImported() {
        return this.offerRepository.count () > 0;
    }

    @Override
    public String readOffersFileContent() throws IOException {
        return Files.readString (path);
    }

    @Override
    public String importOffers() throws IOException, JAXBException {
        ImportOffersDTO offerDTOs = (ImportOffersDTO)
                this.unmarshaller.unmarshal (
                        new FileReader (path.toAbsolutePath ().toString ()));

        return offerDTOs.getOffers ()
                .stream ()
                .map (this::importOffer)
                .collect (Collectors.joining ("\n"));
    }

    private String importOffer(ImportOfferDTO dto) {
        Set<ConstraintViolation<ImportOfferDTO>> validate = validator.validate (dto);

        if (!validate.isEmpty ()) {
            return "Invalid offer";
        }

        Agent agent = this.agentRepository.findByFirstNameOrderByFirstName (String.valueOf (dto.getAgent ().getName ()));
        Apartment apartment = this.apartmentRepository.findByIdOrderById (dto.getApartment ().getId ());

        if(agent != null && apartment != null){

            Offer offer = this.modelMapper.map (dto, Offer.class);

            offer.setAgents (agent);
            offer.setApartments (apartment);

            this.offerRepository.save (offer);

            return String.format ("Successfully imported offer %.2f", offer.getPrice ());
        }else {
            return "Invalid offer";
        }
    }

    @Override
    public String exportOffers() {
        List<Offer> allOffers = this.offerRepository.findAllOffers ();

        return allOffers
                .stream ()
                .map (Offer::toString)
                .collect(Collectors.joining ("\n"));
    }
}
