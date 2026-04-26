package brainbyte.repository;

import brainbyte.model.QuizEntite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<QuizEntite, String> {
    List<QuizEntite> findAllByCategorie(String categorie);
    List<QuizEntite> findAllByCreateurId(String createurId);
}
