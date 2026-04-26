package brainbyte.controller;

import brainbyte.dto.AuthResponseDto;
import brainbyte.dto.LoginRequestDto;
import brainbyte.dto.RegisterRequestDto;
import brainbyte.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthControleur {

    private final AuthService authService;

    public AuthControleur(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/connexion")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginDto) {
        try {
            AuthResponseDto response = authService.login(loginDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/inscription")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto resisterDto) {
        try {
            AuthResponseDto response = authService.register(resisterDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
