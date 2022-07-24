package domaine;

import java.util.HashMap;

public class Carte {


    public static final String ESPACE_BTW_CASES = "    ";
    private HashMap<Integer,HashMap<Integer,Case>> map = new HashMap<>();
    private int colonne;
    private int ligne;


    public Carte(Integer largeur, Integer hauteur){
        this.colonne = largeur;
        this.ligne = hauteur;

        // On remplie la carte avec de la plaine
        for(int i=1; i <= largeur; i++){
            HashMap<Integer, Case> tmpHashmap = new HashMap<>();
            for (int y=1; y <= hauteur; y++){
                tmpHashmap.put(y, new Plaine(i, y));
            }
            map.put(i, tmpHashmap);
        }
    }

    /**
     *      Affiche la carte dans la console
     *
     *      Pour afficher un maximum d'information j'ai décider de d'afficher les cases de la manière suivante :
     *      "[ T2 | P | A N ]" où :
     *          - T2 indique qu'il y a 2 trésors sur la case (T0 si aucun trésors)
     *          - P indique le type de case, dans ce cas là Plaine (M pour une Montagne)
     *          - A indique la présence d'un aventurier ( '-' si aucun aventurier présent)
     *          - N indique son orientation -> Nord : N / Ouest : O / Est : E / Sud : S ( '-' si aucun aventurier présent)
     **/
    public StringBuilder displayMap(){
        StringBuilder strB = new StringBuilder();

        // On affiche la première ligne indiquant les repères de largueur
        strB.append("   ");
        for (int i = 1; i < map.size()+1; i++) {
            // Ajout d'espacces pour s'alligner avec les cases
            strB.append("         ").append(i).append("       ").append(ESPACE_BTW_CASES);
        }

        strB.append("\n");

        for (int i = 1; i < ligne+1; i++) {

            // on construit la ligne à afficher (indice hauteur + suite de cases)
            strB.append(" ").append(i).append("  ");

            for (int j = 1; j < colonne+1; j++) {
                strB.append(map.get(j).get(i).display()).append(ESPACE_BTW_CASES);
            }

            strB.append("\n");
        }

        return strB;
    }

    // Retourne la case en fonction des Coordonnées fournis (colonne & ligne)
    public Case getCaseFromCoord(int colonne, int ligne){
        return this.map.get(colonne).get(ligne);
    }

    public void setCaseFromCoord(int colonne, int ligne, Case c){
        this.map.get(colonne).put(ligne, c);
    }

    public int getColonne() {
        return colonne;
    }

    public int getLigne() {
        return ligne;
    }
}
