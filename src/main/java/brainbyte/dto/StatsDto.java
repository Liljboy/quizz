package brainbyte.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class StatsDto {
    private int totalQuizCrees;
    private double scoreMoyen;
    private List<QuizDto> quizCrees;
}
