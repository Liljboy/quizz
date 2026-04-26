package brainbyte.repository;

import brainbyte.model.OptionReponseEntite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionReponseRepository extends JpaRepository<OptionReponseEntite, String> {
    List<OptionReponseEntite> findAllByIdIn(List<String> ids);
}
