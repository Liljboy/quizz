package brainbyte.dto;

import lombok.Data;

@Data
public class RegisterRequestDto {
    private String pseudo;
    private String email;
    private String motDePasse;
}
