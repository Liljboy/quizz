package brainbyte.dto;

import lombok.Data;
import java.util.Map;

@Data
public class SoumissionQuizDto {
    private String utilisateurId;
    // Map of questionId -> optionId
    private Map<String, String> reponsesSelectionnees;
}
