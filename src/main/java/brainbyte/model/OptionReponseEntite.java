package brainbyte.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "option_reponse")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionReponseEntite {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @ToString.Exclude
    private QuestionEntite question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String texte;

    @Column(name = "est_correcte", nullable = false)
    private Boolean estCorrecte;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = java.util.UUID.randomUUID().toString();
        }
        if (estCorrecte == null) {
            estCorrecte = false;
        }
    }
}
