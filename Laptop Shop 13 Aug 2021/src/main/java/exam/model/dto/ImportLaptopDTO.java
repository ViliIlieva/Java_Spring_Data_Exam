package exam.model.dto;

import exam.model.entity.WarrantyType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class ImportLaptopDTO {

    @Size(min = 8)
    private String macAddress;

    @Positive
    @NotNull
    private double cpuSpeed;

    @Min (8)
    @Max (128)
    private int ram;

    @Min (128)
    @Max (1024)
    private int storage;

    @Size(min = 10)
    private String description;

    @Positive
    @NotNull
    private BigDecimal price;

    @NotNull
    private WarrantyType warrantyType;

    @NotNull
    private NameDTO shop;

}
