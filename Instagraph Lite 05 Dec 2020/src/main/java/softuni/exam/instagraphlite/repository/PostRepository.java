package softuni.exam.instagraphlite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.exam.instagraphlite.models.entity.Post;
import softuni.exam.instagraphlite.models.entity.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
}
