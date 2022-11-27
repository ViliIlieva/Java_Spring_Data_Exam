package exam.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "laptops")
public class Laptop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, name = "mac_address")
    private String macAddress;

    @Column(nullable = false, name = "cpu_speed")
    private double cpuSpeed;

    @Column(nullable = false)
    private int ram;

    @Column(nullable = false)
    private int storage;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false, name = "warranty_type")
    private WarrantyType warrantyType;

    @ManyToOne(optional = false)
    private Shop shop;

    @Override
    public String toString() {
        return "Laptop - " + macAddress + System.lineSeparator () +
                "*Cpu speed - " + cpuSpeed + System.lineSeparator () +
                "**Ram - " + ram + System.lineSeparator () +
                "***Storage - " + storage + System.lineSeparator () +
                "****Price - " + price +System.lineSeparator () +
                "#Shop name - " + shop.getName () + System.lineSeparator () +
                "##Town - " + shop.getTown ().getName ();
    }
}
