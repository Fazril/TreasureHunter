package domaine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Aventurier {

    private String name;
    private List<Tresor> foundTreasure;
    private Integer colonne;
    private Integer ligne;
    private EOrientation orientation;
    private List<String> actionList;
    private EAventurierState state;

    public Aventurier(String name, Integer colonne, Integer ligne, EOrientation orientation, String actionString) {
        this.name = name;
        this.foundTreasure = new ArrayList<>();
        this.colonne = colonne;
        this.ligne = ligne;
        this.orientation = orientation;

        // On sépare la chaine de charactères en entrée pour avoir une liste de String de chaque charactère
        // exemple : "AADAGA" donne -> [ "A", "A", "D", "A", "G", "A" ]
        this.actionList = Arrays.asList(actionString.split("(?!^)").clone());
        this.state = EAventurierState.READY_FOR_NEXT_ACTION;
    }

    public Integer getColonne() {
        return colonne;
    }

    public void setColonne(Integer colonne) {
        this.colonne = colonne;
    }

    public Integer getLigne() {
        return ligne;
    }

    public void setLigne(Integer ligne) {
        this.ligne = ligne;
    }

    public EOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(EOrientation orientation) {
        this.orientation = orientation;
    }

    public String getName() {
        return name;
    }


    public List<String> getActionList() {
        return actionList;
    }

    public void addFoundTreasure(Tresor tresor) {
        foundTreasure.add(tresor);
    }

    public EAventurierState getState() {
        return state;
    }

    public void setState(EAventurierState state) {
        this.state = state;
    }

    /**
     *  Donne la première action de la liste
     *
     * @return  la première action de la liste
     */
    public String giveNextAction(){
        return actionList.get(0);
    }

    /**
     * Action accomplie, on la supprime donc de la liste des actions et on passe l'aventurier à l'état Prêt
     */
    public void actionDone(){
        removeFirstAction();
        state = EAventurierState.READY_FOR_NEXT_ACTION;
    }

    public void removeFirstAction(){
        actionList = actionList.subList(1,actionList.size());
    }

    /**
     * Affiche tous les trésors trouver par l'aventurier et sa valeur total en OR
     */
    public void displayAllTreasuresFoundAndTotalValue(){
        int totalValue = 0;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n------------------------------------------------------\n");
        stringBuilder.append("#### ").append(name).append(" - Treasures found : ")
                .append(foundTreasure.size()).append("  ####\n");
        for (Tresor t: foundTreasure ) {
            stringBuilder.append("    - ").append(t.getNom()).append("\n");
            totalValue += t.getGoldValue();
        }
        stringBuilder.append("\nTotal Value : ").append(totalValue).append(" gold\n");

        System.out.println(stringBuilder.toString());
    }
}
