package softuni.exam.models.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column
    private LocalDateTime date;

    @Column
    private BigDecimal price;

    @ManyToOne(optional = false)
    private Mechanic mechanic;

    @ManyToOne(optional = false)
    @JoinColumn(name = "parts_id")
    private Part part;

    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "cars_id")
    private Car car;

    @Override
    public String toString(){
        return String.format (
                "Car %s %s with %dkm\n" +
                        "-Mechanic: %s %s - task №%d:\n" +
                        "--Engine: %.1f\n" +
                        "---Price: %.2f$",
                this.car.getCarMake (), this.car.getCarModel (), this.car.getKilometers (),
                this.mechanic.getFirstName (), this.mechanic.getLastName (), this.getId (),
                this.car.getEngine (),
                this.getPrice ()
        );
    }

}
