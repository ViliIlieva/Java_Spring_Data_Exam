package softuni.exam.instagraphlite.models.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column
    private String password;

    @Column
    private String username;

    @ManyToOne(optional = false)
    @JoinColumn(name = "profile_picture_id")
    private Picture pictures;

    @OneToMany(mappedBy = "user", targetEntity = Post.class, fetch = FetchType.EAGER)
    List<Post> posts;

    @Override
    public String toString() {
        return "User: " + username + System.lineSeparator () +
                "Post count: " + posts.size ()+ System.lineSeparator () +
                posts.stream ().sorted (Comparator.comparingDouble (p -> p.getPictures ().getSize ()))
                        .map (Post::toString)
                        .collect(Collectors.joining("\n"));
    }
}
