package brainbyte.config;

import brainbyte.model.*;
import brainbyte.repository.*;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final OptionReponseRepository optionReponseRepository;
    private final ScoreRepository scoreRepository;

    public DataInitializer(UtilisateurRepository utilisateurRepository, QuizRepository quizRepository, 
                           QuestionRepository questionRepository, OptionReponseRepository optionReponseRepository,
                           ScoreRepository scoreRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.optionReponseRepository = optionReponseRepository;
        this.scoreRepository = scoreRepository;
    }

    private record QuestionData(String texte, String reponseCorrecte, String f1, String f2, String f3) {}

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("\n====================================================");
        System.out.println("BRAINBYTE : INITIALISATION DES DONNEES...");

        // 1. Créer l'admin si inexistant
        if (!utilisateurRepository.existsById("u1")) {
            UtilisateurEntite admin = new UtilisateurEntite();
            admin.setId("u1");
            admin.setPseudo("Admin");
            admin.setEmail("admin@brainbyte.ht");
            admin.setMotDePasse(BCrypt.hashpw("admin123", BCrypt.gensalt()));
            utilisateurRepository.save(admin);
        }
        UtilisateurEntite admin = utilisateurRepository.findById("u1").get();

        // 2. Nettoyer UNIQUEMENT les quiz de l'admin (pour réinitialiser le contenu officiel)
        List<QuizEntite> adminQuizzes = quizRepository.findAll().stream()
                .filter(q -> q.getCreateur() != null && "u1".equals(q.getCreateur().getId()))
                .toList();
        
        if (!adminQuizzes.isEmpty()) {
            // Supprimer les scores d'abord car ils référencent les quiz
            for (QuizEntite q : adminQuizzes) {
                List<ScoreEntite> scores = scoreRepository.findAll().stream()
                    .filter(s -> s.getQuiz() != null && q.getId().equals(s.getQuiz().getId()))
                    .toList();
                scoreRepository.deleteAll(scores);
            }
            quizRepository.deleteAll(adminQuizzes);
            System.out.println(">>> MISE À JOUR DU CONTENU OFFICIEL...");
        }

        // 3. Injecter les 15 Quiz (75 questions)
        initHistoire(admin);
        initInformatique(admin);
        initMath(admin);
        initPhysique(admin);
        initBiologie(admin);

        System.out.println(">>> 75 QUESTIONS INJECTÉES AVEC SUCCÈS.");
        System.out.println("====================================================\n");
    }

    private void initHistoire(UtilisateurEntite admin) {
        creerQuizFull(admin, "Histoire", "L'Antiquité Romaine", "L'empire qui a façonné l'Europe.", List.of(
            new QuestionData("Qui fut le premier empereur romain ?", "Auguste", "Jules César", "Néron", "Caligula"),
            new QuestionData("En quelle année Rome a-t-elle été fondée ?", "753 av. J.-C.", "509 av. J.-C.", "100 av. J.-C.", "44 av. J.-C."),
            new QuestionData("Quel général carthaginois a traversé les Alpes ?", "Hannibal", "Hamilcar", "Scipion", "Hasdrubal"),
            new QuestionData("Comment s'appelait la place publique centrale ?", "Le Forum", "L'Agora", "Le Capitole", "Le Colisée"),
            new QuestionData("Quel empereur a rendu le christianisme officiel ?", "Théodose Ier", "Constantin", "Tibère", "Trajan")
        ));
        creerQuizFull(admin, "Histoire", "La Révolution Française", "La chute de la monarchie en 1789.", List.of(
            new QuestionData("Quel événement marque le 14 juillet 1789 ?", "La prise de la Bastille", "Le serment du Jeu de Paume", "La fuite à Varennes", "L'exécution du Roi"),
            new QuestionData("Qui était le roi au début de la Révolution ?", "Louis XVI", "Louis XIV", "Louis XV", "Philippe le Bel"),
            new QuestionData("Document célèbre adopté en août 1789 ?", "Droit de l'homme", "Code Civil", "Constitution", "Traité de Versailles"),
            new QuestionData("Période associée à Robespierre ?", "La Terreur", "Le Directoire", "Le Consulat", "La Restauration"),
            new QuestionData("Nom de l'hymne national français ?", "La Marseillaise", "Le Chant du Départ", "L'Ode à la Joie", "Le Temps des Cerises")
        ));
        creerQuizFull(admin, "Histoire", "La Seconde Guerre Mondiale", "Le conflit le plus vaste de l'histoire.", List.of(
            new QuestionData("Déclencheur de la guerre en 1939 ?", "Invasion de la Pologne", "Annexion de l'Autriche", "Pearl Harbor", "Bataille de France"),
            new QuestionData("Premier ministre britannique (1940-45) ?", "Winston Churchill", "Neville Chamberlain", "Clement Attlee", "Anthony Eden"),
            new QuestionData("Date du débarquement en Normandie ?", "6 juin 1944", "8 mai 1945", "11 nov 1942", "15 août 1944"),
            new QuestionData("Ville du premier bombardement atomique ?", "Hiroshima", "Nagasaki", "Tokyo", "Berlin"),
            new QuestionData("Nom de l'alliance (All., It., Jap.) ?", "L'Axe", "La Triple Alliance", "Pacte de Varsovie", "L'Entente Cordiale")
        ));
    }

    private void initInformatique(UtilisateurEntite admin) {
        creerQuizFull(admin, "Informatique", "Les Bases du Web", "HTML, CSS et Internet.", List.of(
            new QuestionData("Que signifie HTML ?", "HyperText Markup Language", "High Tech Multi Language", "Hyperlink Text Management", "Home Tool Markup Language"),
            new QuestionData("Protocole Web sécurisé ?", "HTTPS", "FTP", "SMTP", "SSH"),
            new QuestionData("Propriété CSS pour la couleur du texte ?", "color", "background-color", "font-style", "text-decoration"),
            new QuestionData("Rôle principal de JavaScript ?", "Interactivité", "Structure", "Design", "BDD"),
            new QuestionData("Erreur HTTP 404 ?", "Page non trouvée", "Erreur serveur", "Accès interdit", "OK")
        ));
        creerQuizFull(admin, "Informatique", "Programmation Java", "Concepts fondamentaux.", List.of(
            new QuestionData("Mot-clé pour une classe ?", "class", "void", "public", "new"),
            new QuestionData("Méthode d'entrée principale ?", "main()", "start()", "init()", "run()"),
            new QuestionData("Variable pour nombre entier ?", "int", "String", "boolean", "double"),
            new QuestionData("Concept d'une classe héritant d'une autre ?", "L'héritage", "Encapsulation", "Polymorphisme", "Abstraction"),
            new QuestionData("Outil transformant le code en Bytecode ?", "Le compilateur (javac)", "JVM", "JRE", "IDE")
        ));
        creerQuizFull(admin, "Informatique", "Intelligence Artificielle", "Machine Learning et IA.", List.of(
            new QuestionData("Que signifie IA ?", "Intelligence Artificielle", "Informatique Avancée", "Interface Automatisée", "Indexation Aléatoire"),
            new QuestionData("IA inspirée du cerveau ?", "Réseaux de neurones", "Arbres de décision", "Régression linéaire", "SQL"),
            new QuestionData("Nom du test d'intelligence machine ?", "Test de Turing", "Test de Rorschach", "Test d'Apgar", "Test de Voight-Kampff"),
            new QuestionData("Langage n°1 en IA ?", "Python", "C++", "PHP", "COBOL"),
            new QuestionData("IA génératrice de texte ?", "IA générative", "IA prédictive", "IA réactive", "IA symbolique")
        ));
    }

    private void initMath(UtilisateurEntite admin) {
        creerQuizFull(admin, "Mathématique", "Géométrie", "Théorèmes et formes.", List.of(
            new QuestionData("Théorème des triangles rectangles ?", "Pythagore", "Thalès", "Euclide", "Archimède"),
            new QuestionData("Somme des angles d'un triangle ?", "180°", "90°", "360°", "270°"),
            new QuestionData("Côté le plus long d'un triangle rectangle ?", "L'hypoténuse", "Isocèle", "Diamètre", "Apothème"),
            new QuestionData("Formule aire d'un cercle ?", "π * R²", "2 * π * R", "π * D", "R² / 2"),
            new QuestionData("Côtés d'un hexagone ?", "6", "5", "8", "12")
        ));
        creerQuizFull(admin, "Mathématique", "Algèbre", "Équations et fonctions.", List.of(
            new QuestionData("x dans 2x + 5 = 13 ?", "4", "3", "8", "6"),
            new QuestionData("7 au carré ?", "49", "14", "35", "56"),
            new QuestionData("Nombre du haut d'une fraction ?", "Numérateur", "Dénominateur", "Quotient", "Reste"),
            new QuestionData("Élément neutre de l'addition ?", "0", "1", "-1", "π"),
            new QuestionData("Nombre premier ?", "Divisible par 1 et lui-même", "Nombre pair", "Inférieur à 10", "Une fraction")
        ));
        creerQuizFull(admin, "Mathématique", "Logique", "Probabilités et suites.", List.of(
            new QuestionData("Dé à 6 faces : proba d'un 4 ?", "1/6", "1/2", "1/4", "100%"),
            new QuestionData("3 chats, 3 souris, 3 min... 100 chats, 100 souris ?", "3 min", "100 min", "33 min", "1 min"),
            new QuestionData("Suite : 2, 4, 8, 16... ?", "32", "20", "24", "64"),
            new QuestionData("Résultat d'une multiplication ?", "Produit", "Somme", "Différence", "Quotient"),
            new QuestionData("Symbole de l'infini ?", "∞", "Ω", "Σ", "Δ")
        ));
    }

    private void initPhysique(UtilisateurEntite admin) {
        creerQuizFull(admin, "Physique", "Système Solaire", "Notre galaxie.", List.of(
            new QuestionData("Étoile centrale ?", "Le Soleil", "Sirius", "Étoile Polaire", "Alpha Centauri"),
            new QuestionData("La 'planète rouge' ?", "Mars", "Vénus", "Jupiter", "Saturne"),
            new QuestionData("Plus grosse planète ?", "Jupiter", "Saturne", "Neptune", "Terre"),
            new QuestionData("Force retenant les planètes ?", "Gravité", "Magnétisme", "Friction", "Force centrifuge"),
            new QuestionData("Temps lumière Soleil -> Terre ?", "8 minutes", "1 sec", "1 h", "24 h")
        ));
        creerQuizFull(admin, "Physique", "Énergie", "Forces et lois.", List.of(
            new QuestionData("Formule d'Einstein ?", "E = mc²", "E = 1/2 mv²", "E = mgh", "E = hf"),
            new QuestionData("Unité de puissance électrique ?", "Watt", "Volt", "Ampère", "Ohm"),
            new QuestionData("Qu'est-ce que la réfraction ?", "Déviation de la lumière", "Réflexion", "Disparition", "Chaleur"),
            new QuestionData("Loi de la gravitation ?", "Isaac Newton", "Galilée", "Einstein", "Marie Curie"),
            new QuestionData("Eau bout à (mer) ?", "100°C", "0°C", "50°C", "200°C")
        ));
        creerQuizFull(admin, "Physique", "Quantique", "Le monde minuscule.", List.of(
            new QuestionData("Charge négative ?", "L'électron", "Le proton", "Le neutron", "Le photon"),
            new QuestionData("Expérience du chat ?", "Schrödinger", "Heisenberg", "Planck", "Bohr"),
            new QuestionData("Unité de lumière ?", "Photon", "Atome", "Quartz", "Molécule"),
            new QuestionData("Particule à deux endroits ?", "Superposition", "Multiplication", "Mirage", "Impossible"),
            new QuestionData("Voir les atomes ?", "Microscope à effet tunnel", "Loupe", "Télescope", "Radar")
        ));
    }

    private void initBiologie(UtilisateurEntite admin) {
        creerQuizFull(admin, "Biologie", "Corps Humain", "L'organisme.", List.of(
            new QuestionData("Plus grand organe ?", "La peau", "Le foie", "Les poumons", "Le cerveau"),
            new QuestionData("Rôle des globules rouges ?", "Transporter l'oxygène", "Infections", "Énergie", "Digestion"),
            new QuestionData("Poches cardiaques ?", "4", "2", "3", "1"),
            new QuestionData("Gaz expiré ?", "CO2", "Oxygène", "Azote", "Hydrogène"),
            new QuestionData("Os le plus long ?", "Fémur", "Tibia", "Humérus", "Vertèbre")
        ));
        creerQuizFull(admin, "Biologie", "Monde Animal", "Faune et flore.", List.of(
            new QuestionData("Plus grand mammifère marin ?", "Baleine bleue", "Requin baleine", "Orque", "Dauphin"),
            new QuestionData("Se nourrit de plantes ?", "Herbivore", "Carnivore", "Omnivore", "Détritivore"),
            new QuestionData("Vole en arrière ?", "Colibri", "Aigle", "Pigeon", "Autruche"),
            new QuestionData("Pattes d'araignée ?", "8", "6", "10", "4"),
            new QuestionData("Respirer sous l'eau ?", "Branchies", "Poumons", "Écailles", "Nageoires")
        ));
        creerQuizFull(admin, "Biologie", "Génétique", "Code de la vie.", List.of(
            new QuestionData("Acronyme ADN ?", "Acide DésoxyriboNucléique", "Atome de Nature", "Agent Nutritif", "Acide Durable"),
            new QuestionData("Forme de l'ADN ?", "Double hélice", "Cercle", "Carré", "Ligne"),
            new QuestionData("Lieu de l'ADN ?", "Noyau", "Cytoplasme", "Membrane", "Cils"),
            new QuestionData("Changement de séquence ?", "Mutation", "Sélection", "Respiration", "Clonage"),
            new QuestionData("Paires de chromosomes ?", "23", "46", "20", "10")
        ));
    }

    private void creerQuizFull(UtilisateurEntite admin, String cat, String titre, String desc, List<QuestionData> questions) {
        QuizEntite quiz = new QuizEntite();
        quiz.setId(UUID.randomUUID().toString());
        quiz.setTitre(titre);
        quiz.setDescription(desc);
        quiz.setCategorie(cat);
        quiz.setCreateur(admin);
        quizRepository.save(quiz);

        int ordre = 1;
        for (QuestionData qd : questions) {
            QuestionEntite q = new QuestionEntite();
            q.setId(UUID.randomUUID().toString());
            q.setTexte(qd.texte());
            q.setQuiz(quiz);
            q.setOrdreAffichage(ordre++);
            questionRepository.save(q);

            List<OptionReponseEntite> opts = new ArrayList<>();
            opts.add(creerOption(q, qd.reponseCorrecte(), true));
            opts.add(creerOption(q, qd.f1(), false));
            opts.add(creerOption(q, qd.f2(), false));
            opts.add(creerOption(q, qd.f3(), false));
            optionReponseRepository.saveAll(opts);
        }
    }

    private OptionReponseEntite creerOption(QuestionEntite q, String texte, boolean correct) {
        OptionReponseEntite o = new OptionReponseEntite();
        o.setId(UUID.randomUUID().toString());
        o.setTexte(texte);
        o.setEstCorrecte(correct);
        o.setQuestion(q);
        return o;
    }
}
