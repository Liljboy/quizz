package brainbyte.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuizDto {
    private String id;
    private String titre;
    private String description;
    private String categorie;
    private List<QuestionDto> questions;
}
