package domaine;

import java.util.ArrayList;
import java.util.List;

public class Plaine extends Case{

    private List<Tresor> tresors;
    private Aventurier aventurierPresent;


    public Plaine(int colonne, int ligne) {
        super.state = ECaseState.AVAILABLE;
        this.tresors = new ArrayList<>();
        this.aventurierPresent = null;
        super.colonne = colonne;
        super.ligne = ligne;
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
    @Override
    public String display() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[ ");
        //stringBuilder.append("[ C").append(colonne).append(" L").append(ligne).append(" | ");

        if (tresors.isEmpty()){
            stringBuilder.append("T0");
        }else{
            stringBuilder.append("T").append(tresors.size());
        }

        stringBuilder.append(" | P | ");

        if (aventurierPresent == null){
            stringBuilder.append(" - - ]");
        }else {
            stringBuilder.append(" A ").append(aventurierPresent.getOrientation().getInitiale()).append(" ]");
        }

        return stringBuilder.toString();
    }

    public Aventurier getAventurierPresent() {
        return aventurierPresent;
    }

    public void setAventurierPresent(Aventurier aventurierPresent) {
        this.aventurierPresent = aventurierPresent;
    }

    public List<Tresor> getTresors() {
        return tresors;
    }

    public void setTresors(List<Tresor> tresors) {
        this.tresors = tresors;
    }

    /**
     *       Retourne le premier tresor de la liste et le supprime
     */
    public Tresor giveOneTresor(){
        Tresor tresor = tresors.get(0);
        tresors.remove(0);
        return tresor;
    }
}
