package brainbyte.service;

import brainbyte.dto.AuthResponseDto;
import brainbyte.dto.LoginRequestDto;
import brainbyte.dto.RegisterRequestDto;
import brainbyte.model.UtilisateurEntite;
import brainbyte.repository.UtilisateurRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;

    @org.springframework.beans.factory.annotation.Value("${brainbyte.admin.email}")
    private String adminEmail;

    @org.springframework.beans.factory.annotation.Value("${brainbyte.admin.password}")
    private String adminPassword;

    public AuthService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public AuthResponseDto login(LoginRequestDto dto) {
        // Check if admin
        if (adminEmail.equals(dto.getEmail()) && adminPassword.equals(dto.getMotDePasse())) {
            return new AuthResponseDto("ADMIN-TOKEN", "u1", "Admin", true);
        }

        Optional<UtilisateurEntite> userOpt = utilisateurRepository.findByEmail(dto.getEmail());
        if (userOpt.isPresent()) {
            UtilisateurEntite user = userOpt.get();
            if (BCrypt.checkpw(dto.getMotDePasse(), user.getMotDePasse())) {
                String token = UUID.randomUUID().toString() + "-" + user.getId();
                return new AuthResponseDto(token, user.getId(), user.getPseudo(), false);
            }
        }
        throw new RuntimeException("Email ou mot de passe incorrect");
    }

    public AuthResponseDto register(RegisterRequestDto dto) {
        if (utilisateurRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        UtilisateurEntite baseUser = new UtilisateurEntite();
        baseUser.setPseudo(dto.getPseudo());
        baseUser.setEmail(dto.getEmail());
        baseUser.setMotDePasse(BCrypt.hashpw(dto.getMotDePasse(), BCrypt.gensalt(10)));
        
        UtilisateurEntite savedUser = utilisateurRepository.save(baseUser);
        
        String token = UUID.randomUUID().toString() + "-" + savedUser.getId();
        return new AuthResponseDto(token, savedUser.getId(), savedUser.getPseudo(), false);
    }
}
