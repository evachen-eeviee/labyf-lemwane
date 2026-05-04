package modele.labyrinthe;
import java.util.*;

/**
 * Classe de base représentant un labyrinthe générique.
 * Hérite de {@link Observable} pour notifier les vues (pattern Observer)
 * lors des changements (déplacement du joueur, etc.).
 *
 * <p>Le labyrinthe est composé d’une grille de {@link Case}, possède une entrée
 * en haut, une sortie en bas et gère la position actuelle du joueur.</p>
 *
 */
public class Labyrinthe extends Observable {
    /*---Attributs ---*/
    private final int LARGEUR;
    private final int HAUTEUR;
    private Case[][] grille;
    private int[] coordEntree; //final ?
    private int[] coordSortie= new int[2]; // final ?
    private int[] coordJoueur = new int[2];
    private boolean mouvementBloque = false;

    /*---Constructeur ---*/
    /**
     * Construit un labyrinthe vide avec les dimensions données.
     * L’entrée est placée aléatoirement sur la première ligne (y = 0),
     * entre les colonnes 1 et LARGEUR-2 inclus.
     *
     * @param l largeur du labyrinthe
     * @param h hauteur du labyrinthe
     * @param d niveau de difficulté (non utilisé dans cette classe de base)
     */
    public Labyrinthe(int l, int h, Difficulte d){
        this.LARGEUR=l;
        this.HAUTEUR=h;
        this.grille= new Case[LARGEUR][HAUTEUR];
        for (int x = 0; x < LARGEUR; x++) {
            for (int y = 0; y < HAUTEUR; y++) {
                grille[x][y] = new Case(x, y); // création de l'objet Case
            }
        }
        Random rand = new Random();
        int colEntree = rand.nextInt(LARGEUR-2)+1;
        this.coordEntree = new int[]{colEntree, 0};
    }

    /**
     * Définit les coordonnées de l’entrée et ouvre cette case (supprime le mur).
     *
     * @param x coordonnée x de l’entrée
     * @param y coordonnée y de l’entrée
     */
    public void setCoordEntree(int x, int y){
        coordEntree[0] = x;
        coordEntree[1] = y;
        if (grille[x][y] != null) {
            grille[x][y].setMur(false);
        }
    }

    /**
     * Définit les coordonnées de la sortie et ouvre cette case (supprime le mur).
     *
     * @param x coordonnée x de la sortie
     * @param y coordonnée y de la sortie
     */
    public void setCoordSortie(int x, int y){
        coordSortie[0] = x;
        coordSortie[1] = y;
        if (grille[x][y] != null) {
            grille[x][y].setMur(false);
        }
    }
    
    /**
     * Change la position actuelle du joueur.
     *
     * @param x nouvelle coordonnée x
     * @param y nouvelle coordonnée y
     */
    public void setCoordJoueur(int x, int y){
        this.coordJoueur[0]=x;
        this.coordJoueur[1]=y;
    }

    /*---Getters ---*/
    /** @return les coordonnées {x, y} de l’entrée */
    public int[] getCoordEntree(){return this.coordEntree;}
    
    /** @return les coordonnées {x, y} de la sortie */
    public int[] getCoordSortie(){return this.coordSortie;}
    
    /** 
     * Retourne la case aux coordonnées données.
     * 
     * @param x coordonnée x
     * @param y coordonnée y
     * @return la case correspondante
     */
    public Case getCase(int x, int y) {return grille[x][y];}
    
    /** @return la grille complète du labyrinthe */
    public Case[][] getGrille() {
        return grille;
    }
    
    /** @return la largeur du labyrinthe */
    public int getLARGEUR(){ return this.LARGEUR;}
    
    /** @return la hauteur du labyrinthe */
    public int getHAUTEUR(){return this.HAUTEUR;}
    
    /** @return la position actuelle {x, y} du joueur */
    public int[] getCoordJoueur(){return this.coordJoueur;}

    /*---Méthodes ---*/

   /*Changement de position du joueur dans le labyrinthe*/
   /**
    * Tente de déplacer le joueur dans la direction indiquée.
    * Le déplacement est accepté uniquement si :
    * <ul>
    *   <li>la case cible est dans les limites</li>
    *   <li>le mouvement n’est pas bloqué</li>
    *   <li>la case cible n’a pas de mur (ou c’est la sortie)</li>
    *   <li>la case cible n’est pas l’entrée (impossible de revenir dessus)</li>
    * </ul>
    * Notifie les observateurs après chaque déplacement valide.
    *
    * @param d direction du déplacement
    */
   public void deplacerJoueur(Direction d){

       int[] posJoueur = getCoordJoueur();
       int nx = posJoueur[0];
       int ny= posJoueur[1];
       int[] entree = this.getCoordEntree();
       int[] sortie = this.getCoordSortie();

       switch(d){
           case HAUT:
               ny--;
               break;
           case BAS:
               ny++;
               break;
           case DROITE:
               nx++;
               break;
           case GAUCHE:
               nx--;
       }

       if (nx < 0 || ny < 0 || nx >= getLARGEUR() || ny >= getHAUTEUR()) {
           return;
       }

       if (mouvementBloque) return;

       Case nouvelleCase = getCase(nx, ny);
       int[] nouvelleCaseCoord = nouvelleCase.getCoordonnees();
       boolean estEntree = (nouvelleCaseCoord[0] == entree[0] && nouvelleCaseCoord[1] == entree[1]);
       boolean estSortie = (nouvelleCaseCoord[0] == sortie[0] && nouvelleCaseCoord[1] == sortie[1]);


       if(!nouvelleCase.estMur() && !estEntree || estSortie){
           setCoordJoueur(nx, ny);
       }
       notifierObservateurs();
   }

    /**
     * Bloque temporairement tout déplacement du joueur
     * (utile pendant les animations ou transitions).
     */
    public void bloquerMouvement() {
        this.mouvementBloque = true;
    }

    /**
     * Indique si les déplacements du joueur sont actuellement bloqués.
     *
     * @return true si bloqué, false sinon
     */
    public boolean isMouvementBloque() {
        return this.mouvementBloque;
    }
}