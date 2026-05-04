package modele.labyrinthe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Classe de test de performance pour comparer :
 * <ul>
 *   <li>Le temps de génération de labyrinthes aléatoires (non parfaits) selon le pourcentage de murs</li>
 *   <li>Le coût de construction de la grille d’affichage {@code Case[][]} à partir de la structure optimisée
 *       (tableaux booléens de murs) dans {@link LabyrintheParfait}</li>
 * </ul>
 *
 * <p>Ce programme mesure les temps moyens sur plusieurs essais et affiche les résultats sous forme de tableaux Markdown.</p>
 *
 */
public class TestPerformance {
    
    /**
     * Point d’entrée du programme.
     * Exécute deux séries de benchmarks :
     * <ol>
     *   <li>Influence du pourcentage de murs sur la génération aléatoire</li>
     *   <li>Comparaison entre la création du labyrinthe (structure interne) et la construction de la grille d’affichage</li>
     * </ol>
     *
     * @param args arguments en ligne de commande (non utilisés)
     */
    public static void main(String[] args) {

    int[] ns = {20, 40, 60, 80, 100};
    int essais = 20; 

    System.out.println("#### 4.1 Labyrinthes aléatoires – influence du pourcentage de murs");
    System.out.println("| n   | Taille       | 20 % de murs (ms) | 30 % de murs (ms) | 50 % de murs (ms) |");
    System.out.println("|-----|--------------|-------------------|-------------------|-------------------|");

    for (int n : ns) {
        int largeur = n;
        int hauteur = 2 * n;

        double t20 = mesureTempsAleatoire(largeur, hauteur, 0.20, essais);
        double t30 = mesureTempsAleatoire(largeur, hauteur, 0.30, essais);
        double t50 = mesureTempsAleatoire(largeur, hauteur, 0.50, essais);

        System.out.printf("| %3d | %3d × %3d | %17.0f | %17.0f | %17.0f |\n", n, largeur, hauteur, t20, t30, t50);
    }

    System.out.println("\n#### 4.2 Labyrinthes parfaits – comparaison des structures de données");
    System.out.println("| n   | Taille       | Structure TP2 tableaux booléens (ms) | Structure Case[][] (ms) | Gain     |");
    System.out.println("|-----|--------------|--------------------------------------|--------------------------|----------|");

    for (int n : ns) {
        int largeur = n;
        int hauteur = 2 * n;

        long totalBool = 0;
        long totalCase = 0;

        for (int i = 0; i < essais; i++) {
            long debutBool = System.nanoTime();
            LabyrintheParfait lab = new LabyrintheParfait(largeur, hauteur, hauteur * 0.6);
            long finBool = System.nanoTime();
            totalBool += (finBool - debutBool);

            long debutCase = System.nanoTime();
            lab.construireGrilleDepuisMurs();
            long finCase = System.nanoTime();
            totalCase += (finCase - debutCase);
        }

        double avgBool = totalBool / (double) essais / 1_000_000.0;
        double avgCase = totalCase / (double) essais / 1_000_000.0;
        double gain = avgCase / avgBool;

        System.out.printf("| %3d | %3d × %3d | %36.2f | %24.2f | ×%-6.2f |\n",
                n, largeur, hauteur, avgBool, avgCase, gain);
    }
}


    /**
     * Mesure le temps moyen nécessaire pour générer un labyrinthe aléatoire
     * (non parfait) avec un pourcentage donné de murs conservés.
     * <p>
     * La génération consiste à :
     * <ul>
     *   <li>Initialiser tous les murs à {@code true}</li>
     *   <li>Construire une liste de tous les murs possibles</li>
     *   <li>Mélanger cette liste et retirer un nombre calculé de murs</li>
     * </ul>
     *
     * @param largeur         largeur du labyrinthe
     * @param hauteur          hauteur du labyrinthe
     * @param pourcentageMurs  proportion de murs à conserver (ex: 0.20 = 20%)
     * @param essais           nombre d’exécutions pour calculer la moyenne
     * @return temps moyen en millisecondes
     */
    private static double mesureTempsAleatoire(int largeur, int hauteur, double pourcentageMurs, int essais) {
        long total = 0;
        Random r = new Random(123); 

        for (int i = 0; i < essais; i++) {
            long debut = System.nanoTime();

            boolean[][] mursV = new boolean[largeur - 1][hauteur];
            boolean[][] mursH = new boolean[hauteur - 1][largeur];

            int totalMurs = (largeur - 1) * hauteur + (hauteur - 1) * largeur;
            int mursARetirer = (int) (totalMurs * (1.0 - pourcentageMurs));

            
            for (boolean[] row : mursV) Arrays.fill(row, true);
            for (boolean[] row : mursH) Arrays.fill(row, true);

            
            List<int[]> listeMurs = new ArrayList<>();
            for (int x = 0; x < largeur - 1; x++)
                for (int y = 0; y < hauteur; y++)
                    listeMurs.add(new int[]{x, y, 0}); 
            for (int x = 0; x < largeur; x++)
                for (int y = 0; y < hauteur - 1; y++)
                    listeMurs.add(new int[]{x, y, 1}); 

            Collections.shuffle(listeMurs, r);
            for (int k = 0; k < mursARetirer; k++) {
                int[] m = listeMurs.get(k);
                if (m[2] == 0) mursV[m[0]][m[1]] = false;
                else mursH[m[1]][m[0]] = false;
            }

            long fin = System.nanoTime();
            total += (fin - debut);
        }
        return total / (double) essais / 1_000_000.0;
    }
}