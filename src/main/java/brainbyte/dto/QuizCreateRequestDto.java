package brainbyte.dto;

import java.util.List;

public class QuizCreateRequestDto {
    private String titre;
    private String description;
    private String categorie;
    private List<QuestionCreateDto> questions;

    // Getters and Setters
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public List<QuestionCreateDto> getQuestions() { return questions; }
    public void setQuestions(List<QuestionCreateDto> questions) { this.questions = questions; }

    public static class QuestionCreateDto {
        private String texte;
        private List<OptionCreateDto> options;

        public String getTexte() { return texte; }
        public void setTexte(String texte) { this.texte = texte; }
        public List<OptionCreateDto> getOptions() { return options; }
        public void setOptions(List<OptionCreateDto> options) { this.options = options; }
    }

    public static class OptionCreateDto {
        private String texte;
        private boolean correcte;

        public String getTexte() { return texte; }
        public void setTexte(String texte) { this.texte = texte; }
        public boolean isCorrecte() { return correcte; }
        public void setCorrecte(boolean correcte) { this.correcte = correcte; }
    }
}
