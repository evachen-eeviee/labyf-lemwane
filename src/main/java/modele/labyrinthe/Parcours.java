package modele.labyrinthe;

import java.util.*;

/**
 * Classe utilitaire responsable de la génération d’un chemin garanti dans un labyrinthe
 * et du calcul du plus court chemin entre l’entrée et la sortie.
 *
 * <p>Cette classe était probablement destinée à créer un labyrinthe tressé ou semi-parfait
 * avec un chemin principal forcé, mais contient actuellement plusieurs anomalies
 * (inversions x/y, logique incomplète, etc.). Elle reste néanmoins fonctionnelle
 * dans certains cas simples.</p>
 *
 */
public class Parcours {
    private Labyrinthe lab;

    /**
     * Construit un objet Parcours attaché à un labyrinthe donné.
     *
     * @param lab le labyrinthe sur lequel opérer
     */
    public Parcours(Labyrinthe lab){
        this.lab=lab;
    }

    /**
     * Génère un chemin principal garanti depuis l’entrée jusqu’à une case proche du bas,
     * puis fixe la sortie sur ou juste en dessous de la dernière case du chemin.
     * <p>
     * Attention : cette méthode contient des incohérences (inversions x/y dans les boucles,
     * conditions de voisinage complexes, sortie parfois mal placée), mais est laissée
     * telle quelle car demandée sans modification du code.
     * </p>
     *
     * @return la liste des cases formant le chemin généré (de l’entrée vers le bas)
     */
    public List<Case> genererChemin(){
        List<Case> chemin = new ArrayList<>();
        Set<Case> cheminRapide = new HashSet<>(); // pour contains + rapide

        int x = this.lab.getCoordEntree()[0];  //colonne de la grille
        int y = this.lab.getCoordEntree()[1]; // ligne
        Case courant = this.lab.getCase(x,y);

        chemin.add(courant);
        cheminRapide.add(courant);

        // Ajouter la case juste en dessous
        courant = this.lab.getCase(x, y + 1);
        chemin.add(courant);
        cheminRapide.add(courant);

        int largeur = this.lab.getLARGEUR();
        int hauteur = this.lab.getHAUTEUR();
        boolean sortieFixee = false;
        Random rand = new Random();

        while (!sortieFixee) {
            // Récupère les voisins valides pas encore dans le chemin
            List<Case> voisins = getVoisinsChemin(courant, cheminRapide);
            List<Case> voisinsValides = new ArrayList<>();

            for (Case c : voisins) {
                int cx = c.getCoordonnees()[0];
                if (!cheminRapide.contains(c) && cx > 0 && cx < largeur - 1) {
                    voisinsValides.add(c);
                }
            }

            if (voisinsValides.isEmpty()) break; // pas de voisin valide, arrêt

            // Choisir un voisin aléatoire
            courant = voisinsValides.get(rand.nextInt(voisinsValides.size()));
            chemin.add(courant);
            cheminRapide.add(courant);

            // Si on atteint le bas - 1 (donc -2) --> fixer la sortie
            if (courant.getCoordonnees()[1] == hauteur - 2) {
                this.lab.setCoordSortie(courant.getCoordonnees()[0], hauteur - 1);
                sortieFixee = true;
            }

            if (!sortieFixee) {
                Case fin = chemin.get(chemin.size() - 1);
                int[] coordFin = fin.getCoordonnees();
                int sortieX = coordFin[0];
                int sortieY = Math.min(coordFin[1] + 1, hauteur - 1);
                this.lab.setCoordSortie(sortieX, sortieY);
            }
        }

        return chemin;
    }

    /**
     * Retourne les voisins possibles pour étendre le chemin principal.
     * <p>Attention : inversion x/y dans les directions (d[1] pour x, d[0] pour y).</p>
     *
     * @param c case courante
     * @param cheminRapide ensemble des cases déjà dans le chemin
     * @return liste des voisins candidats
     */
    public List<Case> getVoisinsChemin(Case c, Set<Case> cheminRapide){
        List<Case> voisins = new ArrayList<>();

        int x = c.getCoordonnees()[0];
        int y = c.getCoordonnees()[1];
        int largeur = this.lab.getLARGEUR();
        int hauteur = this.lab.getHAUTEUR();

        int[][] directions = {{1,0},{0,1},{0,-1}}; // droite, bas, haut

        for(int[] d : directions){
            int nx = x + d[1];
            int ny = y + d[0];

            if(nx >= 0 && nx < largeur && ny >= 0 && ny < hauteur){
                Case voisin = this.lab.getCase(nx, ny);

                if(!cheminRapide.contains(voisin) && voisinValideChemin(voisin, cheminRapide)){
                    voisins.add(voisin);
                }
            }
        }

        return voisins;
    }

    /**
     * Vérifie qu’un voisin n’est pas entouré de trop de cases déjà dans le chemin
     * (évite les boucles ou les murs trop denses).
     * <p>Logique complexe avec double boucle et compteur de voisins déjà visités.</p>
     *
     * @param c case à tester
     * @param cheminRapide ensemble des cases du chemin principal
     * @return true si la case est acceptable comme extension du chemin
     */
    public boolean voisinValideChemin(Case c, Set<Case> cheminRapide){
        int x = c.getCoordonnees()[0];
        int y = c.getCoordonnees()[1];

        for(int i=-1; i<=0; i++){
            for(int j=-1; j<=0;j++){
                int cx = x + i;
                int cy = y + j;

                if(cx >=0 && cy>=0 && cx+1< this.lab.getLARGEUR() && cy +1 < lab.getHAUTEUR()){
                    int cpt=0;
                    if(cheminRapide.contains(this.lab.getCase(cx,cy))) cpt++;
                    if(cheminRapide.contains(this.lab.getCase(cx,cy+1))) cpt++;
                    if(cheminRapide.contains(this.lab.getCase(cx+1,cy))) cpt++;
                    if(cheminRapide.contains(this.lab.getCase(cx+1,cy+1))) cpt++;

                    if(cpt>2) return false;
                }
            }
        }
        return true;
    }


    /**
     * Calcule le plus court chemin de l’entrée à la sortie à l’aide d’un parcours en largeur (BFS).
     * <p>Marque les cases visitées avec {@code setEstVisitee(true)} et utilise un prédécesseur.</p>
     *
     * @return liste des cases du plus court chemin, de la sortie vers l’entrée
     *         (le premier élément est la sortie, le dernier est l’entrée)
     */
    public List<Case> plusCourtChemin(){
        Queue<Case> file = new LinkedList<>();

        int[] coordEntree = this.lab.getCoordEntree();
        int[] coordSortie = this.lab.getCoordSortie();

        Case depart = this.lab.getCase(coordEntree[0],coordEntree[1]);
        Case sortie = this.lab.getCase(coordSortie[0],coordSortie[1]);
        sortie.setMur(false);

        depart.setEstVisitee(true);
        file.add(depart);

        boolean trouve = false;
        while(!trouve && !file.isEmpty()){
            Case courante = file.peek();
            if(courante.equals(sortie)){
                trouve=true;
            }else{
                List<Case> voisins = getVoisinsParcours(courante);
                for(Case c : voisins){
                    if(!c.estMur() && !c.estVisitee()){
                        c.setEstVisitee(true);
                        c.setPredecesseur(courante);
                        file.offer(c);
                    }
                }

                file.poll();
                //System.out.println("File :"+file);
            }
        }

        List<Case> parcours = new ArrayList<>();

        Case courante = sortie;

        while(courante !=null){
            parcours.add(courante);
            courante = courante.getPredecesseur();
        }

        return parcours;
    }

    /**
     * Retourne toutes les cases adjacentes accessibles (sans mur) autour d’une case donnée.
     * <p>Attention : inversion x/y dans les indices (d[1] pour x, d[0] pour y).</p>
     *
     * @param c case centrale
     * @return liste des voisins accessibles
     */
    public List<Case> getVoisinsParcours(Case c){
        List<Case> voisins = new ArrayList<>();

        int x = c.getCoordonnees()[0];
        int y = c.getCoordonnees()[1];
        int largeur = this.lab.getLARGEUR();
        int hauteur = this.lab.getHAUTEUR();

        int[][] directions = {{1,0},{0,1},{0,-1},{-1,0}}; //Toutes les cases autour de c

        for(int[] d : directions) {
            int nx = x + d[1];
            int ny = y + d[0];

            if ((nx >= 0 && nx < largeur) && (ny >= 0 && ny < hauteur)) {
                Case voisine = this.lab.getCase(nx,ny);
                if(!voisine.estMur())
                    voisins.add(voisine);
            }
        }
        //System.out.println("Voisins :"+voisins);
        return voisins;
    }


}