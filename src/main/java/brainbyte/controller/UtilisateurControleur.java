package brainbyte.controller;

import brainbyte.dto.StatsDto;
import brainbyte.service.UtilisateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/utilisateurs")
// Dans une API sécurisée standard avec Spring Security, l'ID viendrait du Principal.
// Ici on passe le userId explicitement dans les headers ou URL pour le MVP.
public class UtilisateurControleur {

    private final UtilisateurService utilisateurService;

    public UtilisateurControleur(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<StatsDto> obtenirStatistiques(@PathVariable String id) {
        return ResponseEntity.ok(utilisateurService.obtenirStatistiques(id));
    }
}
