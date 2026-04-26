package brainbyte.controller;

import brainbyte.dto.QuizDto;
import brainbyte.dto.ResultatQuizDto;
import brainbyte.dto.SoumissionQuizDto;
import brainbyte.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizControleur {

    private final QuizService quizService;

    public QuizControleur(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping
    public ResponseEntity<List<QuizDto>> listerQuiz(@RequestParam(required = false) String categorie) {
        return ResponseEntity.ok(quizService.listerQuizs(categorie));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizDto> obtenirQuiz(@PathVariable String id) {
        try {
            return ResponseEntity.ok(quizService.recupererQuiz(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/soumettre")
    public ResponseEntity<ResultatQuizDto> soumettreQuiz(@PathVariable String id, @RequestBody SoumissionQuizDto soumission) {
        return ResponseEntity.ok(quizService.soumettreQuiz(id, soumission));
    }

    @PostMapping("/creer")
    public ResponseEntity<Void> creerQuiz(@RequestParam String userId, @RequestBody brainbyte.dto.QuizCreateRequestDto dto) {
        quizService.creerQuiz(userId, dto);
        return ResponseEntity.ok().build();
    }
}
