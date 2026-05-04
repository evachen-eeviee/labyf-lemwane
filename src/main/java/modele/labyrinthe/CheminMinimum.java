package modele.labyrinthe;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Classe utilitaire permettant de générer un labyrinthe parfait avec un chemin minimum garanti
 * entre l'entrée et la sortie, ainsi que de calculer le plus court chemin dans un labyrinthe parfait.
 * 
 * Cette classe utilise des algorithmes de génération (Kruskal modifié + chemin forcé) et de résolution
 * tout en accédant à des membres privés de {@link LabyrintheParfait}
 * via la réflexion Java.
 * 
 */
public class CheminMinimum {

    /**
     * Classe interne représentant une cellule du labyrinthe par ses coordonnées (x, y).
     * Utilisée principalement pour l'algorithme Union-Find de Kruskal.
     */
    private static class Cell {
        int x, y;
        
        /** Construit une cellule avec les coordonnées données. */
        Cell(int x, int y) { this.x = x; this.y = y; }
        
        @Override public boolean equals(Object o) {
            if (!(o instanceof Cell c)) return false;
            return x == c.x && y == c.y;
        }
        
        @Override public int hashCode() { return x * 1000 + y; }
    }

    /**
     * Recherche le représentant (racine) d'une cellule dans la structure Union-Find
     * avec compression de chemin.
     * 
     * @param parent map associant chaque cellule à son parent
     * @param c la cellule dont on cherche la racine
     * @return la racine de l'ensemble contenant c
     */
    private static Cell find(Map<Cell, Cell> parent, Cell c) {
        if (parent.get(c) != c) {
            parent.put(c, find(parent, parent.get(c)));
        }
        return parent.get(c);
    }

    /**
     * Génère un labyrinthe parfait avec un chemin d'au moins {@code longueurCheminMin} cases
     * entre l'entrée (en haut) et la sortie (en bas).
     * 
     * <p>La méthode combine deux approches :</p>
     * <ul>
     *   <li>Un chemin long forcé est créé au début depuis l'entrée</li>
     *   <li>L'algorithme de Kruskal est ensuite utilisé pour connecter toutes les cases restantes</li>
     *   <li>La sortie est placée sur la cellule du bas la plus éloignée de l'entrée</li>
     * </ul>
     * 
     * <p>L'accès aux champs privés de {@link LabyrintheParfait} se fait par réflexion.</p>
     * 
     * @param largeur largeur du labyrinthe (nombre de colonnes)
     * @param hauteur hauteur du labyrinthe (nombre de lignes)
     * @param longueurCheminMin longueur minimale garantie du chemin solution (doit être >= 2)
     * @param difficulte paramètre de difficulté (non utilisé dans cette version)
     * @return un {@link LabyrintheParfait} avec un chemin solution d'au moins {@code longueurCheminMin} cases
     * @throws IllegalArgumentException si {@code longueurCheminMin < 2}
     */
    public static Labyrinthe genererAvecCheminMinimum(int largeur, int hauteur,
                                                     int longueurCheminMin, Difficulte difficulte) {
        if (longueurCheminMin < 2) throw new IllegalArgumentException(">=2");

        Random rand = new Random();

        LabyrintheParfait laby = new LabyrintheParfait(largeur, hauteur, 0);

        try {
            // === Accès aux champs privés via réflexion ===
            Field mursVField = LabyrintheParfait.class.getDeclaredField("mursVerticaux");
            Field mursHField = LabyrintheParfait.class.getDeclaredField("murHorizontaux");
            Field coordEntreeField = LabyrintheParfait.class.getDeclaredField("coordEntree");
            Field coordSortieField = LabyrintheParfait.class.getDeclaredField("coordSortie");
            Field coordJoueurField = LabyrintheParfait.class.getDeclaredField("coordJoueur");

            mursVField.setAccessible(true);
            mursHField.setAccessible(true);
            coordEntreeField.setAccessible(true);
            coordSortieField.setAccessible(true);
            coordJoueurField.setAccessible(true);

            // Réinitialiser les murs
            boolean[][] mursV = new boolean[largeur - 1][hauteur];
            boolean[][] mursH = new boolean[hauteur - 1][largeur];
            for (boolean[] row : mursV) Arrays.fill(row, true);
            for (boolean[] row : mursH) Arrays.fill(row, true);

            mursVField.set(laby, mursV);
            mursHField.set(laby, mursH);

            // Entrée
            int entreeX = rand.nextInt(largeur - 2) + 1;
            coordEntreeField.set(laby, new int[]{entreeX, 0});
            coordJoueurField.set(laby, new int[]{entreeX, 0});

            // === Chemin long garanti ===
            Set<Cell> visite = new HashSet<>();
            List<Cell> cheminLong = new ArrayList<>();
            Cell courant = new Cell(entreeX, 1);
            cheminLong.add(courant);
            visite.add(courant);

            int[][] dirs = {{0,1},{1,0},{0,-1},{-1,0}};

            while (cheminLong.size() < longueurCheminMin - 1) {
                List<Cell> voisins = new ArrayList<>();
                for (int[] d : dirs) {
                    int nx = courant.x + d[0];
                    int ny = courant.y + d[1];
                    if (nx >= 1 && nx < largeur-1 && ny >= 1 && ny < hauteur-1) {
                        Cell v = new Cell(nx, ny);
                        if (!visite.contains(v)) voisins.add(v);
                    }
                }
                if (voisins.isEmpty()) break;

                courant = voisins.get(rand.nextInt(voisins.size()));
                cheminLong.add(courant);
                visite.add(courant);

                Cell prev = cheminLong.get(cheminLong.size() - 2);
                if (courant.x == prev.x) {
                    int minY = Math.min(courant.y, prev.y);
                    mursH[minY][courant.x] = false;
                } else {
                    int minX = Math.min(courant.x, prev.x);
                    mursV[minX][courant.y] = false;
                }
            }

            // === Kruskal pour connecter tout ===
            List<int[]> aretes = new ArrayList<>();
            for (int x = 1; x < largeur-1; x++) {
                for (int y = 1; y < hauteur-1; y++) {
                    if (x < largeur-2) aretes.add(new int[]{x, y, x+1, y});
                    if (y < hauteur-2) aretes.add(new int[]{x, y, x, y+1});
                }
            }
            Collections.shuffle(aretes, rand);

            Map<Cell, Cell> parent = new HashMap<>();
            for (int x = 1; x < largeur-1; x++)
                for (int y = 1; y < hauteur-1; y++)
                    parent.put(new Cell(x,y), new Cell(x,y));

            for (int[] a : aretes) {
                Cell c1 = new Cell(a[0], a[1]);
                Cell c2 = new Cell(a[2], a[3]);
                if (find(parent, c1) != find(parent, c2)) {
                    parent.put(find(parent, c1), find(parent, c2));
                    if (a[0] == a[2]) {
                        mursH[Math.min(a[1], a[3])][a[0]] = false;
                    } else {
                        mursV[Math.min(a[0], a[2])][a[1]] = false;
                    }
                }
            }

            // === Choisir la sortie la plus éloignée ===
            int meilleurX = largeur / 2;
            int maxDist = -1;

            // On utilise la méthode privée via réflexion
            java.lang.reflect.Method distanceMethod = LabyrintheParfait.class
                .getDeclaredMethod("distanceDepuisEntree", int.class, int.class);
            distanceMethod.setAccessible(true);

            for (int x = 1; x < largeur - 1; x++) {
                int d = (int) distanceMethod.invoke(laby, x, hauteur - 2);
                if (d > maxDist && d < Integer.MAX_VALUE) {
                    maxDist = d;
                    meilleurX = x;
                }
            }

            if (maxDist < longueurCheminMin * 0.6 && !cheminLong.isEmpty()) {
                meilleurX = cheminLong.get(cheminLong.size() - 1).x;
            }

            coordSortieField.set(laby, new int[]{meilleurX, hauteur - 1});

            // Reconstruire l'affichage
            laby.construireGrilleDepuisMurs();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return laby;
    }

    /**
     * Calcule le plus court chemin entre l'entrée et la sortie dans un labyrinthe parfait
     * à l'aide d'un parcours en largeur (BFS).
     * 
     * <p>Le chemin retourné inclut à la fois l'entrée et la sortie.</p>
     * <p>La liste est construite de la sortie vers l'entrée (premier élément = sortie, dernier = entrée).
     * Il est conseillé d'inverser la liste si l'ordre entrée → sortie est préféré.</p>
     * 
     * @param lab le labyrinthe parfait dans lequel chercher le chemin
     * @return une liste de tableaux {@code int[]{x, y}} représentant les cases du plus court chemin
     */
    public static List<int[]> plusCourtCheminParfait(LabyrintheParfait lab) {

        int largeur = lab.getLARGEUR();
        int hauteur = lab.getHAUTEUR();

        int[] entree = lab.getCoordEntree();
        int[] sortie = lab.getCoordSortie();

        Queue<int[]> file = new LinkedList<>();
        Map<String, int[]> parent = new HashMap<>(); // clé sous la forme : "x,y" et après int[] pr les coord (values)

        file.add(entree);
        parent.put(entree[0] + "," + entree[1], null); // entrée n'a pas de parent pcq c'est le début

        int[][] directions = { {1,0}, {-1,0}, {0,1}, {0,-1} };
        boolean trouve = false;

        while (!file.isEmpty() && !trouve) {
            int[] courant = file.poll();
            int x = courant[0], y = courant[1];

            if (x == sortie[0] && y == sortie[1]) {
                trouve = true;
            }

            for (int[] d : directions) {
                int nx = x + d[0];
                int ny = y + d[1];
                String key = nx + "," + ny;

                // si coordonnées correctes + pas visité + pas de mur
                if (nx >= 0 && ny >= 0 && nx < largeur && ny < hauteur && !parent.containsKey(key) && !lab.murEntreCases(x, y, nx, ny)) {
                    file.add(new int[]{nx, ny});
                    parent.put(key, new int[]{x, y}); // on stocke les coordonnées dans la map
                }
            }
        }

        // Reconstruction du chemin depuis la sortie
        List<int[]> chemin = new ArrayList<>();
        String key = sortie[0] + "," + + sortie[1];

        boolean fini = false;
        while (!fini) {
            String[] parts = key.split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            chemin.add(new int[]{x, y});

            int[] p = parent.get(key);
            if (p == null) {
                fini = true;
            } else {
                key = p[0] + "," + p[1];
            }
        }
        return chemin;
    }
}