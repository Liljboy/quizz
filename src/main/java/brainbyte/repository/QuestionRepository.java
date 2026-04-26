package brainbyte.repository;

import brainbyte.model.QuestionEntite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntite, String> {
    List<QuestionEntite> findAllByQuizId(String quizId);
}
