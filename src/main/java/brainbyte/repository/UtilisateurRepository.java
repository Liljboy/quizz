package brainbyte.repository;

import brainbyte.model.UtilisateurEntite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<UtilisateurEntite, String> {
    Optional<UtilisateurEntite> findByEmail(String email);
}
