package exam.service.impl;

import exam.model.dto.ImportShopDTO;
import exam.model.dto.ImportShopWrapperDTO;
import exam.model.dto.ImportTownWrapperDTO;
import exam.model.entity.Shop;
import exam.model.entity.Town;
import exam.repository.ShopRepository;
import exam.repository.TownRepository;
import exam.service.ShopService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ShopServiceImpl implements ShopService {
    private final Path path = Path.of("src/main/resources/files/xml/shops.xml");

    private final ShopRepository shopRepository;
    private final TownRepository townRepository;

    private final Unmarshaller unmarshaller;
    private final Validator validator;
    private final ModelMapper modelMapper;

    @Autowired
    public ShopServiceImpl(ShopRepository shopRepository, TownRepository townRepository) throws JAXBException {
        this.shopRepository = shopRepository;
        this.townRepository = townRepository;

        JAXBContext context = JAXBContext.newInstance (ImportShopWrapperDTO.class);
        this.unmarshaller = context.createUnmarshaller ();
        this.validator = Validation.buildDefaultValidatorFactory ().getValidator ();
        this.modelMapper = new ModelMapper ();
    }

    @Override
    public boolean areImported() {
        return this.shopRepository.count () > 0;
    }

    @Override
    public String readShopsFileContent() throws IOException {
        return Files.readString (path);
    }

    @Override
    public String importShops() throws JAXBException, FileNotFoundException {
        ImportShopWrapperDTO shopDTOs = (ImportShopWrapperDTO) this.unmarshaller.unmarshal (
                new FileReader (path.toAbsolutePath ().toString ()));

        return shopDTOs.getShops ().stream ()
                .map (this::importShop)
                .collect(Collectors.joining ("\n"));
    }

    private String importShop(ImportShopDTO dto) {
        Set<ConstraintViolation<ImportShopDTO>> validate = validator.validate (dto);

        if (!validate.isEmpty ()) {
            return "Invalid shop";
        }

        if(this.shopRepository.findByName (dto.getName ()).isPresent ()){
            return "Invalid shop";
        }

        Shop shop = this.modelMapper.map (dto, Shop.class);
        Optional<Town> town = this.townRepository.findByName (dto.getTown ().getName ());
        shop.setTown (town.get ());
        this.shopRepository.save (shop);

        return String.format ("Successfully imported Shop %s - %s",
                shop.getName (), shop.getIncome ());
    }



















}
