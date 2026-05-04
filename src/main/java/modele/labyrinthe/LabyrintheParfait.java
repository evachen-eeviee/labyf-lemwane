package modele.labyrinthe;

import java.util.*;

/**
 * Représente un labyrinthe parfait (sans boucle, une seule solution).
 * Hérite de {@link Labyrinthe} et implémente une structure basée sur des tableaux
 * de murs horizontaux et verticaux.
 *
 * <p>Le labyrinthe est généré avec une distance minimale garantie entre l’entrée
 * et la sortie grâce au paramètre {@code CHEMINMIN}.</p>
 *
 * <p>La grille d’affichage (utilisée par la vue) peut être reconstruite à partir
 * des murs via {@link #construireGrilleDepuisMurs()}.</p>
 *
 */
public class LabyrintheParfait extends Labyrinthe{
//    private final int super.getLARGEUR(), super.getHAUTEUR();
    private final double CHEMINMIN;
    public boolean[][] mursVerticaux;
    public boolean[][] murHorizontaux;
    private int[] coordEntree;
    private Case[][] grille;
    private int[] coordSortie;
    private int[] coordJoueur;

    /**
     * Construit un labyrinthe parfait vide avec tous les murs fermés.
     * L’entrée et la sortie sont placées aléatoirement sur les bordures haute et basse.
     *
     * @param largeur  largeur du labyrinthe (nombre de cases)
     * @param hauteur  hauteur du labyrinthe (nombre de cases)
     * @param cheminMin distance minimale souhaitée entre entrée et sortie
     */
    public LabyrintheParfait(int largeur, int hauteur, double cheminMin){
        super(largeur,hauteur,null);
        this.CHEMINMIN = cheminMin;

        this.mursVerticaux = new boolean[super.getLARGEUR()-1][super.getHAUTEUR()];
        this.murHorizontaux= new boolean[super.getHAUTEUR()-1][super.getLARGEUR()];

        for(int i = 0; i<this.getLARGEUR()-1;i++){
            for(int j = 0; j<super.getHAUTEUR();j++){
                this.mursVerticaux[i][j]=true;
            }
        }

        for(int i = 0; i<super.getHAUTEUR()-1; i++){
            for(int j = 0; j<super.getLARGEUR();j++){
                this.murHorizontaux[i][j]=true;
            }
        }

        
        Random rand = new Random();
        this.coordEntree = new int[]{rand.nextInt( super.getLARGEUR()-2)+1, 0};
        this.coordSortie = new int[]{rand.nextInt( super.getLARGEUR()-2)+1, super.getHAUTEUR()-1};
        this.coordJoueur = new int[]{coordEntree[0], coordEntree[1]};
    }

    /*Getters et Setters */

    /** @return les coordonnées {x, y} de l’entrée */
    public int[] getCoordEntree() {
        return coordEntree;
    }

    /** @return les coordonnées {x, y} de la sortie */
    public int[] getCoordSortie() {
        return coordSortie;
    }

    /** @param coordSortie nouvelles coordonnées de la sortie */
    public void setCoordSortie(int[] coordSortie) {
        this.coordSortie = coordSortie;
    }

    /** @param coordEntree nouvelles coordonnées de l’entrée */
    public void setCoordEntree(int[] coordEntree) {
        this.coordEntree = coordEntree;
    }

    /** @return la position actuelle {x, y} du joueur */
    public int[] getCoordJoueur() {
        return coordJoueur;
    }

    /** @param coordJoueur nouvelle position du joueur */
    public void setCoordJoueur(int[] coordJoueur) {
        this.coordJoueur = coordJoueur;
    }

    /** @return la hauteur du labyrinthe */
    public int getHAUTEUR() {
        return super.getHAUTEUR();
    }

    /** @return la largeur du labyrinthe */
    public int getLARGEUR() {
        return super.getLARGEUR();
    }

    /*Méthodes :*/

    /**
     * Supprime le mur entre deux cases adjacentes.
     *
     * @param x1 x de la première case
     * @param y1 y de la première case
     * @param x2 x de la seconde case
     * @param y2 y de la seconde case
     */
    public void enleverMur(int x1, int y1, int x2, int y2){ //coordonnées des deux cellules
        if(y1==y2){
            int minX = Math.min(x1, x2);
            mursVerticaux[minX][y1] = false;
        }else if(x1==x2){
            int minY = Math.min(y1, y2);
            murHorizontaux[minY][x1] = false;
        }
    }

    /**
     * Affiche le labyrinthe dans la console avec des caractères ASCII.
     * E = entrée, S = sortie, espaces = chemin, | et - = murs.
     */
    public void afficher() {
        // Affichage la ligne du haut
        for (int x = 0; x < super.getLARGEUR(); x++) System.out.print("+---");
        System.out.println("+");

        for (int y = 0; y < super.getHAUTEUR(); y++) {
            // murs verticaux
            for (int x = 0; x < super.getLARGEUR(); x++) {
                // Mur vertical gauche
                if (x == 0) System.out.print("|");

                // Contenu de la cellule
                if (coordEntree[0] == x && coordEntree[1] == y) System.out.print(" E ");
                else if (coordSortie[0] == x && coordSortie[1] == y) System.out.print(" S ");
                else System.out.print("   ");

                // Mur vertical à droite (sauf dernière cellule, déjà fermé à la fin)
                if (x < super.getLARGEUR() - 1) {
                    if (mursVerticaux[x][y]) System.out.print("|");
                    else System.out.print(" ");
                }
            }
            System.out.println("|"); // mur droit de la dernière cellule

            // murs horizontaux
            for (int x = 0; x < super.getLARGEUR(); x++) {
                System.out.print("+");
                if (y < super.getHAUTEUR() - 1 && murHorizontaux[y][x]) System.out.print("---");
                else System.out.print("   ");
            }
            System.out.println("+");
        }

        // Ligne finale en bas du labyrinthe (ne pas compter les "+" au dessus qui servent à rien)
        for (int x = 0; x < super.getLARGEUR(); x++) System.out.print("+---");
        System.out.println("+");
    }

    /**
     * Calcule la distance minimale (en nombre de cases) entre l’entrée et la case donnée.
     * Utilise un BFS interne.
     *
     * @param sx coordonnée x de la case cible
     * @param sy coordonnée y de la case cible
     * @return distance depuis l’entrée, ou {@link Integer#MAX_VALUE} si inaccessible
     */
    private int distanceDepuisEntree(int sx, int sy) {
        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[super.getLARGEUR()][super.getHAUTEUR()];

        queue.add(coordEntree);
        visited[coordEntree[0]][coordEntree[1]] = true;

        Map<String, Integer> dist = new HashMap<>();
        dist.put(coordEntree[0] + "," + coordEntree[1], 0);

        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

        while(!queue.isEmpty()){
            int[] c = queue.poll();
            int x = c[0], y = c[1];
            int d = dist.get(x + "," + y);

            if(x == sx && y == sy) return d;

            for(int[] dir : dirs){
                int nx = x + dir[0];
                int ny = y + dir[1];

                if(nx >= 0 && nx < super.getLARGEUR() && ny >= 0 && ny < super.getHAUTEUR()
                        && !visited[nx][ny]
                        && !murEntreCases(x,y,nx,ny)) {

                    visited[nx][ny] = true;
                    queue.add(new int[]{nx, ny});
                    dist.put(nx + "," + ny, d + 1);
                }
            }
        }

        return Integer.MAX_VALUE;
    }

    /**
     * Vérifie s’il existe un mur entre deux cases adjacentes.
     *
     * @param x1 x de la première case
     * @param y1 y de la première case
     * @param x2 x de la seconde case
     * @param y2 y de la seconde case
     * @return true s’il y a un mur, false sinon
     */
    public boolean murEntreCases(int x1, int y1, int x2, int y2){
        if(x1 == x2){ // déplacement vertical
            int minY = Math.min(y1, y2);
            return murHorizontaux[minY][x1];
        } else { // déplacement horizontal
            int minX = Math.min(x1, x2);
            return mursVerticaux[minX][y1];
        }
    }

    /**
     * Déplace le joueur dans la direction indiquée si aucun mur ne bloque le passage.
     * Notifie les observateurs après un déplacement valide.
     *
     * @param dir direction du déplacement
     */
    public void deplacerJoueur(Direction dir) {
        int x = coordJoueur[0];
        int y = coordJoueur[1];
        int nx = x, ny = y;

        switch(dir) {
            case HAUT -> ny--;
            case BAS -> ny++;
            case GAUCHE -> nx--;
            case DROITE -> nx++;
        }

        // Vérifie si on reste dans le labyrinthe
        if(nx < 0 || ny < 0 || nx >= super.getLARGEUR() || ny >= super.getHAUTEUR()) return;

        // Vérifie si un mur bloque le déplacement
        if(murEntreCases(x, y, nx, ny)) return;

        // Déplace le joueur
        coordJoueur[0] = nx;
        coordJoueur[1] = ny;

        // Notifie les observateurs pour mise à jour de la vue
        notifierObservateurs();
    }


    /**
     * Place la sortie sur une case du bas respectant la distance minimale {@link #CHEMINMIN}.
     * Lance une exception si aucune case valide n’est trouvée.
     */
    private void placerSortieAvecDistanceMin() {
        List<int[]> candidats = new ArrayList<>();

        // cases du bas seulement (y = super.getHAUTEUR() - 1)
        for(int x = 1; x < super.getLARGEUR() - 1; x++){
            int d = distanceDepuisEntree(x, super.getHAUTEUR() - 1);
            if(d >= CHEMINMIN){
                candidats.add(new int[]{x, super.getHAUTEUR() - 1});
            }
        }

        if(candidats.isEmpty()){
            throw new IllegalStateException("Impossible de placer une sortie à distance >= " + CHEMINMIN);
        }

        Random r = new Random();
        this.coordSortie = candidats.get(r.nextInt(candidats.size()));
    }

   /**
    * Construit une grille d’affichage agrandie (avec murs visibles) à partir des tableaux
    * de murs horizontaux et verticaux. Utilisée pour l’affichage graphique.
    */
   public void construireGrilleDepuisMurs() {
        int W = super.getLARGEUR()/2;
        int H = super.getHAUTEUR()/2;

        // Nouvelle grille 2D avec murs et chemins (taille d'affichage)
        Case[][] g = new Case[W * 2 + 1][H * 2 + 1];

        // Tout est mur par défaut
        for (int x = 0; x < g.length; x++) {
            for (int y = 0; y < g[0].length; y++) {
                g[x][y] = new Case(x, y);
                g[x][y].setMur(true);
            }
        }

        // Déposer les cellules (chemins)
        for (int cx = 0; cx < W; cx++) {
            for (int cy = 0; cy < H; cy++) {
                int gx = cx * 2 + 1;
                int gy = cy * 2 + 1;
                g[gx][gy].setMur(false); // case accessible
            }
        }

        // Déposer les ouvertures horizontales
        for (int x = 0; x < W; x++) {
            for (int y = 0; y < H - 1; y++) {
                if (!murHorizontaux[y][x]) {
                    int gx = x * 2 + 1;
                    int gy = y * 2 + 2;
                    g[gx][gy].setMur(false);
                }
            }
        }

        // Déposer les ouvertures verticales
        for (int x = 0; x < W - 1; x++) {
            for (int y = 0; y < H; y++) {
                if (!mursVerticaux[x][y]) {
                    int gx = x * 2 + 2;
                    int gy = y * 2 + 1;
                    g[gx][gy].setMur(false);
                }
            }
        }
        this.grille = g;
    }
    
    /** @return la grille d’affichage construite (ou null si pas encore appelée) */
    public Case[][] getGrille() { return grille; }

}