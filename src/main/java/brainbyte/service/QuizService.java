package brainbyte.service;

import brainbyte.dto.*;
import brainbyte.model.OptionReponseEntite;
import brainbyte.model.QuestionEntite;
import brainbyte.model.QuizEntite;
import brainbyte.model.ScoreEntite;
import brainbyte.model.UtilisateurEntite;
import brainbyte.repository.OptionReponseRepository;
import brainbyte.repository.QuizRepository;
import brainbyte.repository.ScoreRepository;
import brainbyte.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final brainbyte.repository.QuestionRepository questionRepository;
    private final OptionReponseRepository optionReponseRepository;
    private final ScoreRepository scoreRepository;
    private final UtilisateurRepository utilisateurRepository;

    public QuizService(QuizRepository quizRepository, 
                       brainbyte.repository.QuestionRepository questionRepository,
                       OptionReponseRepository optionReponseRepository,
                       ScoreRepository scoreRepository,
                       UtilisateurRepository utilisateurRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.optionReponseRepository = optionReponseRepository;
        this.scoreRepository = scoreRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public void creerQuiz(String utilisateurId, QuizCreateRequestDto dto) {
        UtilisateurEntite user = utilisateurRepository.findById(utilisateurId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        QuizEntite quiz = new QuizEntite();
        quiz.setId(java.util.UUID.randomUUID().toString());
        quiz.setTitre(dto.getTitre());
        quiz.setDescription(dto.getDescription());
        quiz.setCategorie(dto.getCategorie());
        quiz.setCreateur(user);
        quizRepository.save(quiz);

        if (dto.getQuestions() != null) {
            int ordre = 1;
            for (QuizCreateRequestDto.QuestionCreateDto qDto : dto.getQuestions()) {
                QuestionEntite q = new QuestionEntite();
                q.setId(java.util.UUID.randomUUID().toString());
                q.setTexte(qDto.getTexte());
                q.setQuiz(quiz);
                q.setOrdreAffichage(ordre++);
                questionRepository.save(q);

                if (qDto.getOptions() != null) {
                    for (QuizCreateRequestDto.OptionCreateDto oDto : qDto.getOptions()) {
                        OptionReponseEntite o = new OptionReponseEntite();
                        o.setId(java.util.UUID.randomUUID().toString());
                        o.setTexte(oDto.getTexte());
                        o.setEstCorrecte(oDto.isCorrecte());
                        o.setQuestion(q);
                        optionReponseRepository.save(o);
                    }
                }
            }
        }
    }

    public List<QuizDto> listerQuizs(String categorie) {
        List<QuizEntite> quizEntites = (categorie != null && !categorie.isEmpty()) 
            ? quizRepository.findAllByCategorie(categorie) 
            : quizRepository.findAll();
        
        return quizEntites.stream().map(q -> {
            QuizDto dto = new QuizDto();
            dto.setId(q.getId());
            dto.setTitre(q.getTitre());
            dto.setDescription(q.getDescription());
            dto.setCategorie(q.getCategorie());
            return dto;
        }).collect(Collectors.toList());
    }

    public QuizDto recupererQuiz(String quizId) {
        QuizEntite quizEntite = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz non trouvé"));
        
        QuizDto quizDto = new QuizDto();
        quizDto.setId(quizEntite.getId());
        quizDto.setTitre(quizEntite.getTitre());
        quizDto.setDescription(quizEntite.getDescription());
        quizDto.setCategorie(quizEntite.getCategorie());
        
        List<QuestionDto> questionsDto = new ArrayList<>();
        for (QuestionEntite q : quizEntite.getQuestions()) {
            QuestionDto qDto = new QuestionDto();
            qDto.setId(q.getId());
            qDto.setTexte(q.getTexte());
            qDto.setOrdreAffichage(q.getOrdreAffichage());
            
            List<OptionReponseDto> optionsDto = new ArrayList<>();
            for (OptionReponseEntite o : q.getOptions()) {
                OptionReponseDto oDto = new OptionReponseDto();
                oDto.setId(o.getId());
                oDto.setTexte(o.getTexte());
                optionsDto.add(oDto);
            }
            qDto.setOptions(optionsDto);
            questionsDto.add(qDto);
        }
        quizDto.setQuestions(questionsDto);
        
        return quizDto;
    }

    public ResultatQuizDto soumettreQuiz(String quizId, SoumissionQuizDto soumission) {
        QuizEntite quizEntite = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz non trouvé"));
        
        Map<String, String> reponsesSoumises = soumission.getReponsesSelectionnees() != null 
                ? soumission.getReponsesSelectionnees() : new HashMap<>();
        
        // Charger les options soumises pour verifier leur validité
        List<OptionReponseEntite> optionsChoisies = optionReponseRepository.findAllByIdIn(new ArrayList<>(reponsesSoumises.values()));
        
        int score = 0;
        int total = quizEntite.getQuestions().size();
        
        for (OptionReponseEntite option : optionsChoisies) {
            if (option.getEstCorrecte() != null && option.getEstCorrecte()) {
                score++;
            }
        }
        
        double pourcentage = total == 0 ? 0 : ((double) score / total) * 100;
        String message = pourcentage >= 50 ? "Félicitations, vous avez réussi !" : "Dommage, il faudra réviser un peu plus.";

        if (soumission.getUtilisateurId() != null) {
            UtilisateurEntite user = utilisateurRepository.findById(soumission.getUtilisateurId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            
            ScoreEntite scoreEntite = new ScoreEntite();
            scoreEntite.setUtilisateur(user);
            scoreEntite.setQuiz(quizEntite);
            scoreEntite.setScore(score);
            scoreEntite.setTotal(total);
            scoreRepository.save(scoreEntite);
        }

        return ResultatQuizDto.builder()
                .score(score)
                .totalQuestions(total)
                .pourcentage(pourcentage)
                .message(message)
                .build();
    }
}
