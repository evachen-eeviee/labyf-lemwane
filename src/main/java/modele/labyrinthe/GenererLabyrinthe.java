package modele.labyrinthe;

import java.util.*;

/**
Classe Genererlabyrinthe
 */
public class GenererLabyrinthe {

    /**
     * Génère un labyrinthe avec les dimensions et le pourcentage de murs spécifiés.
     *
     * @param largeur Largeur du labyrinthe.
     * @param hauteur Hauteur du labyrinthe.
     * @param pourcentageMurs Pourcentage de cellules qui seront des murs (entre 0 et 1).
     * @return Un labyrinthe généré avec un chemin garanti.
     */
    public static Labyrinthe genererAlea(int largeur, int hauteur, double pourcentageMurs) {
        Labyrinthe laby = new Labyrinthe(largeur, hauteur, null);
        Parcours prc = new Parcours(laby);
        List<Case> chemin = prc.genererChemin();

        for(Case c : chemin){
            c.setMur(false);
            c.setChemin(true);
        }

        Random rand = new Random();

        // Création de la grille
        for (int x = 0; x < largeur; x++) {
            for (int y = 0; y < hauteur; y++) {
                Case c = laby.getCase(x, y);

                // Murs sur les côtés
                if(!c.estChemin()){
                    if (x == 0 || x == largeur - 1 || y == 0 || y == hauteur - 1) {
                        c.setMur(true);
                    }
                    // Murs aléatoires à l'intérieur
                    else {
                        c.setMur(rand.nextDouble() < pourcentageMurs);
                    }

                    laby.getGrille()[x][y] = c;
                }
            }
        }
        return laby;
    }


    // Basé sur le TP :
    public static LabyrintheParfait genererParfait(int largeur, int hauteur, double distance) {
        LabyrintheParfait laby = new LabyrintheParfait(largeur, hauteur, distance);

        boolean[][] visite = new boolean[largeur][hauteur];
        Stack<int[]> pile = new Stack<>();

        Random rand = new Random();

        int[] coordEntree = laby.getCoordEntree();
        int ex = coordEntree[0];
        int ey = coordEntree[1];

        visite[ex][ey] = true;
        pile.push(new int[]{ex, ey});

        while (!pile.isEmpty()) {
            int[] courant = pile.peek();
            int x = courant[0];
            int y = courant[1];

            List<int[]> voisins = new ArrayList<>();

            if (y > 0 && !visite[x][y - 1]) voisins.add(new int[]{x, y - 1});
            if (y < hauteur - 1 && !visite[x][y + 1]) voisins.add(new int[]{x, y + 1});
            if (x > 0 && !visite[x - 1][y]) voisins.add(new int[]{x - 1, y});
            if (x < largeur - 1 && !visite[x + 1][y]) voisins.add(new int[]{x + 1, y});

            if (!voisins.isEmpty()) {
                int[] v = voisins.get(rand.nextInt(voisins.size()));
                int vx = v[0];
                int vy = v[1];

                // supprime le mur entre courant et voisin
                laby.enleverMur(x, y, vx, vy);

                visite[vx][vy] = true;
                pile.push(v);
            } else {
                pile.pop();
            }
        }

        return laby;
    }


//    public static void main(String[] args) {
//        LabyrintheParfait lab = GenererLabyrinthe.genererParfait(15, 20,10);
//        System.out.println("Chemin minimal : "+CheminMinimum.plusCourtCheminParfait(lab).size());
//        lab.afficher();
//
//
//
////        CheminMinimum.genererAvecCheminMinimum(5, 5, 11, Difficulte.FACILE);
////        lab.afficher();
//    }
}