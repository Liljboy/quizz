package brainbyte.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResultatQuizDto {
    private int score;
    private int totalQuestions;
    private double pourcentage;
    private String message;
}
