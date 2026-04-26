package brainbyte.controller;

import brainbyte.repository.QuizRepository;
import brainbyte.repository.ScoreRepository;
import brainbyte.repository.UtilisateurRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UtilisateurRepository utilisateurRepository;
    private final QuizRepository quizRepository;
    private final ScoreRepository scoreRepository;
    
    @org.springframework.beans.factory.annotation.Value("${brainbyte.admin.email}")
    private String adminEmail;

    public AdminController(UtilisateurRepository utilisateurRepository, QuizRepository quizRepository, ScoreRepository scoreRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.quizRepository = quizRepository;
        this.scoreRepository = scoreRepository;
    }

    @GetMapping("/stats")
    public Map<String, Object> getAdminStats(@org.springframework.web.bind.annotation.RequestHeader(value = "Authorization", required = false) String auth) {
        // Pour ce MVP, on vérifie simplement la présence du "token" d'admin envoyé par le front
        if (auth == null || !auth.startsWith("ADMIN-TOKEN")) {
            throw new RuntimeException("Accès refusé : Identifiants admin requis");
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", utilisateurRepository.count());
        stats.put("totalQuizzes", quizRepository.count());
        stats.put("totalScores", scoreRepository.count());
        
        // On récupère les 5 derniers quiz créés en évitant les références circulaires
        List<Map<String, Object>> recentList = quizRepository.findAll().stream()
                .filter(q -> q.getDateCreation() != null)
                .sorted((q1, q2) -> q2.getDateCreation().compareTo(q1.getDateCreation()))
                .limit(5)
                .map(q -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("titre", q.getTitre());
                    map.put("categorie", q.getCategorie());
                    map.put("pseudo", q.getCreateur() != null ? q.getCreateur().getPseudo() : "Admin");
                    return map;
                })
                .toList();
                
        stats.put("recentQuizzes", recentList);
                
        return stats;
    }
}
