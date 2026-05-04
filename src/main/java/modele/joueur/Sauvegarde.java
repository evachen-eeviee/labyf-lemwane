package modele.joueur;

import modele.labyrinthe.Difficulte;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import modele.joueur.*;

/**
Classe Sauvegarde
 */
public class Sauvegarde {
    /** Chemin du fichier CSV pour stocker les profils. */
    private static final String PATH_FILE_CSV = "profil_joueur.csv";
    /** Dossier pour les fichiers sérialisés. */
    private static final String DATA_FOLDER = "data";
    /** Nom du fichier sérialisé. */
    private static final String FILE_SER = "profil_joueur.ser";

    /**
     * Sauvegarde le profil d'un joueur et sa progression dans un fichier CSV.
     * 
     *
     * @param joueur Le joueur à sauvegarder.
     * @param progression La progression du joueur. CSV.
     */
    public static void sauvegarderProfil(Joueur joueur, Progression progression) {
        File csvFile = new File(PATH_FILE_CSV);
        List<String> lines = new ArrayList<>();
        boolean updated = false;

        // Lire les lignes existantes
        if (csvFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(";");
                    if (parts.length > 0 && parts[0].equals(joueur.getNom())) {
                        // Remplacer la ligne pour ce joueur
                        lines.add(buildCsvLine(joueur, progression));
                        updated = true;
                    } else {
                        lines.add(line);
                    }
                }
            } catch (IOException ioe) {
                System.err.println("Erreur lors de la lecture du fichier CSV : " + ioe.getMessage());
            }
        }

        // Si le joueur n'existait pas, ajouter une nouvelle ligne
        if (!updated) {
            lines.add(buildCsvLine(joueur, progression));
        }

        // Réécrire le fichier CSV
        try (PrintWriter writer = new PrintWriter(new FileWriter(PATH_FILE_CSV))) {
            for (String line : lines) {
                writer.println(line);
            }
            System.out.println("Sauvegarde du profil et de l'avancement réussie dans " + PATH_FILE_CSV);
        } catch (IOException ioe) {
            System.err.println("Erreur lors de la sauvegarde du profil : " + ioe.getMessage());
        }
    }

    /**
     * Construit une ligne CSV pour un joueur et sa progression.
     *
     * @param joueur Le joueur à sauvegarder.
     * @param progression La progression du joueur
     * @return La ligne CSV formatée.
     * 
     */
    private static String buildCsvLine(Joueur joueur, Progression progression) {
        StringBuilder sb = new StringBuilder();
        sb.append(joueur.getNom()).append(";")
          .append(joueur.getMotDePasse()).append(";")
          .append(joueur.getScore()).append(";");

        Map<Integer, Map<Difficulte, Boolean>> validation = progression.getValidation();
        for (int i = 0; i < progression.getEtapes().size(); i++) {
            Map<Difficulte, Boolean> diffValidation = validation.get(i);
            sb.append("etape ").append(i + 1).append(" facile:")
              .append(diffValidation.get(Difficulte.FACILE)).append(";");
            sb.append("etape ").append(i + 1).append(" normal:")
              .append(diffValidation.get(Difficulte.NORMAL)).append(";");
            sb.append("etape ").append(i + 1).append(" difficile:")
              .append(diffValidation.get(Difficulte.DIFFICILE));
            if (i < progression.getEtapes().size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
    
    /**
     * Charge le profil d'un joueur à partir d'un fichier CSV en fonction de son nom et mot de passe.
     *
     * @param nom Nom du joueur.
     * @param motDePasse Mot de passe du joueur.
     * @return Un tableau contenant le joueur et sa progression, ou {null, null} si le chargement échoue.
     * 
     */
    public static Object[] chargerProfil(String nom, String motDePasse) {
        try (BufferedReader reader = new BufferedReader(new FileReader(PATH_FILE_CSV))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length < 6) {
                    System.err.println("Format de fichier CSV invalide pour une ligne.");
                    continue;
                }

                String savedNom = parts[0];
                String savedMotDePasse = parts[1];
                int score;
                try {
                    score = Integer.parseInt(parts[2]);
                } catch (NumberFormatException e) {
                    System.err.println("Erreur de format pour le score : " + e.getMessage());
                    continue;
                }

                if (savedNom.equals(nom) && verifierMotDePasse(savedMotDePasse, motDePasse)) {
                    // Préparation des structures vides
                    List<Etape> etapes = new ArrayList<>();
                    etapes.add(new Etape(10, 10));
                    etapes.add(new Etape(20, 20));
                    etapes.add(new Etape(40, 40));

                    Map<Integer, Map<Difficulte, Boolean>> validation = new HashMap<>();
                    for (int i = 0; i < etapes.size(); i++) {
                        Map<Difficulte, Boolean> diffValidation = new HashMap<>();
                        diffValidation.put(Difficulte.FACILE, false);
                        diffValidation.put(Difficulte.NORMAL, false);
                        diffValidation.put(Difficulte.DIFFICILE, false);
                        validation.put(i, diffValidation);
                    }

                    Progression progression = new Progression(etapes, 1, validation);   

                    if (parts.length > 6) {
                        String[] etapeParts = parts[6].split(",");
                        for (String etapePart : etapeParts) {
                            String[] keyValue = etapePart.split(":");
                            if (keyValue.length != 2) {
                                continue;
                            }
                            String key = keyValue[0].trim();
                            boolean value = Boolean.parseBoolean(keyValue[1].trim());

                            if (key.startsWith("etape ")) {
                                String[] keyParts = key.split(" ");
                                if (keyParts.length != 6) {
                                    continue;
                                }
                                int etapeNum = Integer.parseInt(keyParts[1]) - 1;
                                String diffStr = keyParts[2].toUpperCase();
                                Difficulte diff;
                                try {
                                    diff = Difficulte.valueOf(diffStr);
                                } catch (IllegalArgumentException e) {
                                    continue;
                                }

                                if (value) {
                                    progression.validerDefi(etapeNum, diff);
                                }
                            }
                        }
                    }

                    Joueur joueur = new Joueur(nom, progression, score, motDePasse);
                    System.out.println("Chargement du profil et de l'avancement réussi pour " + nom);
                    return new Object[]{joueur, progression};
                }
            }
            System.out.println("Nom ou mot de passe incorrect.");
            return new Object[]{null, null};
        } catch (FileNotFoundException fnfe) {
            System.err.println("Fichier CSV non trouvé : " + fnfe.getMessage());
        } catch (IOException ioe) {
            System.err.println("Erreur lors du chargement du profil : " + ioe.getMessage());
        }
       

        return new Object[]{null, null};
    }

    /**
     * Crée un nouveau joueur et sauvegarde son profil dans le fichier CSV.
     *
     * @param nom Nom du nouveau joueur.
     * @param motDePasse Mot de passe du nouveau joueur.
     */
    public static void creerEtSauvegarderJoueur(String nom, String motDePasse) {
        Joueur joueur = new Joueur(nom, motDePasse);
        Progression progression = new Progression();
        sauvegarderProfil(joueur, progression);
    }

    /**
     * Vérifie si le mot de passe fourni correspond au mot de passe enregistré.
     *
     * @param savedMotDePasse Mot de passe enregistré dans le fichier.
     * @param motDePasse Mot de passe fourni pour la vérification.
     * @return true si les mots de passe correspondent, false sinon.
     */
    public static boolean verifierMotDePasse(String savedMotDePasse, String motDePasse) {
        return savedMotDePasse.equals(motDePasse);
    }

    /**
     * Sauvegarde une liste de joueurs dans un fichier sérialisé .
     *
     * @param joueurs Liste des joueurs à sauvegarder 
     */
    public static void sauvegardeSer(List<Joueur> joueurs) {
        File dossier = new File(DATA_FOLDER);
        if (!dossier.exists()) {
            dossier.mkdirs();
        }

        File fichier = new File(dossier, FILE_SER);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fichier))) {
            oos.writeObject(joueurs);
            oos.flush();
        } catch (IOException ioe) {
            System.err.println("Erreur lors de la sauvegarde : " + ioe);
        }
    }

    /**
     * Charge une liste de joueurs à partir d'un fichier sérialisé   
     *
     * @return La liste des joueurs chargés, ou une liste vide si le chargement échoue.

     */
    public static List<Joueur> chargerSer() {
        File fichier = new File(DATA_FOLDER, FILE_SER);
        if (!fichier.exists()) {
            System.err.println("Aucun fichier existant.");
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichier))) {
            List<Joueur> listeJoueurs = (List<Joueur>) ois.readObject();
            return listeJoueurs;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur lors du chargement : " + e);
            return new ArrayList<>();
        }
    }

    /**
     * Ajoute un joueur à la liste des joueurs dans un fichier sérialisé.
     *
     * @param joueur Joueur à ajouter.
     */
    private static void ajouterJoueurSer(Joueur joueur) {
        List<Joueur> listeJoueurs = chargerSer();
        listeJoueurs.add(joueur);
        sauvegardeSer(listeJoueurs);
    }

    /**
     * Met à jour un profil de joueur dans un fichier sérialisé
     *
     * @param joueur Joueur à mettre à jour.
     */
    public static void majJoueurSer(Joueur joueur) {
        List<Joueur> listeJoueurs = chargerSer();
        listeJoueurs.remove(joueur);
        listeJoueurs.add(joueur);
        sauvegardeSer(listeJoueurs);
    }

    /**
     * Méthode principale pour tester la sauvegarde et le chargement d'un joueur.
     ** @param args Arguments de la ligne de commande
     */
    public static void main(String[] args) {
        Joueur victor = new Joueur("Victor", "mdp123");
        victor.ajouterScore(500);

        Progression progression = victor.getProgression();

        for (Difficulte d : Difficulte.values()) {
            progression.validerDefi(0, d);
        }

        Sauvegarde.majJoueurSer(victor);

        List<Joueur> joueurs = Sauvegarde.chargerSer();
        System.out.println("✅ Joueurs enregistrés :");
        for (Joueur j : joueurs) {
            System.out.println(" - " + j.getNom() + " (score: " + j.getScore() + ")");
            afficherProgression(j);
        }
    }

    /**
     * Affiche la progression d'un joueur pour les besoins de test.
     *
     * @param joueur Joueur dont la progression doit être affichée.
     */
    private static void afficherProgression(Joueur joueur) {
        Progression prog = joueur.getProgression();
        List<Etape> etapes = prog.getEtapes();
        for (int i = 0; i < etapes.size(); i++) {
            Etape e = etapes.get(i);
            System.out.print("   Étape " + (i + 1) + " : ");
            for (Difficulte d : Difficulte.values()) {
                Defi defi = e.getDefi(d);
                System.out.print(d + "=" + (defi.isTermine() ? "✔" : "✖") + " ");
            }
            System.out.println();
        }
    }
}