package softuni.exam.models.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "offers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private BigDecimal price;


    @Column(nullable = false, name = "published_on")
    private LocalDate publishedOn;

    @ManyToOne(optional = false)
    private Apartment apartments;

    @ManyToOne(optional = false)
    private Agent agents;

    @Override
    public String toString() {
        return String.format (
                "Agent %s %s with offer №%d:\n" +
                        "\t-Apartment area: %.2f\n" +
                        "\t--Town: %s\n" +
                        "\t---Price: %.2f$",
                this.getAgents ().getFirstName (),
                this.getAgents ().getLastName (),
                this.getId (),
                this.getApartments ().getArea (),
                this.getApartments ().getTowns ().getTownName (),
                this.price);
    }
}
