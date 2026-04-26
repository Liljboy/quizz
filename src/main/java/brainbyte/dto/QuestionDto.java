package brainbyte.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuestionDto {
    private String id;
    private String texte;
    private Integer ordreAffichage;
    private List<OptionReponseDto> options;
}
