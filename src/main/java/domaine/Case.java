package domaine;

public abstract class Case {

    protected ECaseState state;
    protected Integer colonne;
    protected Integer ligne;

    public ECaseState getState(){
        return state;
    }

    public void setState(ECaseState state) {
        this.state = state;
    }

    /**
     *      Donne les informations concernant la case pour l'affichage
     *
     *      Pour afficher un maximum d'information j'ai décider de d'afficher les cases de la manière suivante :
     *      "[ T2 | P | A N ]" où :
     *          - T2 indique qu'il y a 2 trésors sur la case (T0 si aucun trésors)
     *          - P indique le type de case, dans ce cas là Plaine (M pour une Montagne)
     *          - A indique la présence d'un aventurier ( '-' si aucun aventurier présent / "x" si Montagne)
     *          - N indique son orientation -> Nord : N / Ouest : O / Est : E / Sud : S ( '-' si aucun aventurier présent / "x" si Montagne)
     **/
    public String display(){
        return "";
    }

    public Integer getColonne() {
        return colonne;
    }

    public Integer getLigne() {
        return ligne;
    }
}
