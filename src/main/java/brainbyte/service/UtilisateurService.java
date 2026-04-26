package brainbyte.service;

import brainbyte.dto.QuizDto;
import brainbyte.dto.StatsDto;
import brainbyte.model.QuizEntite;
import brainbyte.model.ScoreEntite;
import brainbyte.repository.QuizRepository;
import brainbyte.repository.ScoreRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UtilisateurService {

    private final ScoreRepository scoreRepository;
    private final QuizRepository quizRepository;

    public UtilisateurService(ScoreRepository scoreRepository, QuizRepository quizRepository) {
        this.scoreRepository = scoreRepository;
        this.quizRepository = quizRepository;
    }

    public StatsDto obtenirStatistiques(String utilisateurId) {
        List<ScoreEntite> scores = scoreRepository.findAllByUtilisateurId(utilisateurId);
        List<QuizEntite> quizCrees = quizRepository.findAllByCreateurId(utilisateurId);

        double scoreMoyen = 0;
        if (!scores.isEmpty()) {
            double totalPourcentage = scores.stream()
                .mapToDouble(s -> s.getTotal() == 0 ? 0 : ((double) s.getScore() / s.getTotal()) * 100)
                .sum();
            scoreMoyen = totalPourcentage / scores.size();
        }

        List<QuizDto> quizDtos = quizCrees.stream().map(q -> {
            QuizDto dto = new QuizDto();
            dto.setId(q.getId());
            dto.setTitre(q.getTitre());
            dto.setDescription(q.getDescription());
            dto.setCategorie(q.getCategorie());
            return dto;
        }).collect(Collectors.toList());

        return StatsDto.builder()
                .totalQuizCrees(quizCrees.size())
                .scoreMoyen(scoreMoyen)
                .quizCrees(quizDtos)
                .build();
    }
}
