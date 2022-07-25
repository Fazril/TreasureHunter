package domaine;

import exceptions.CantForwardException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static domaine.EAventurierState.*;

public class Aventurier{

    private String name;
    private List<Tresor> foundTreasure;
    private Integer colonne;
    private Integer ligne;
    private EOrientation orientation;
    private List<String> actionList;
    private EAventurierState state;
    private Carte map;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

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

    public List<Tresor> getFoundTreasure() {
        return foundTreasure;
    }

    public EAventurierState getState() {
        return state;
    }

    public void setState(EAventurierState state) {
        this.state = state;
    }

    public void setMap(Carte map) {
        this.map = map;
    }


    /* ---------------------------------------------------------------------------------------------------------------*/

    public void beginAdventure(CountDownLatch countDownLatch) {
        // On execute la méthode toutes les secondes
        executorService.scheduleAtFixedRate(() -> handleAction(countDownLatch), 0, 1, TimeUnit.SECONDS);
    }

    /**
     *  Prend en charge l'action de l'aventurier
     */
    private void handleAction(CountDownLatch countDownLatch){

        switch (state){
            case IN_ACTION:
                pickUpTreasor((Plaine) map.getCaseFromCoord(colonne, ligne) );
                break;
            case WAITING_TO_ACT:
            case READY_FOR_NEXT_ACTION:
                doAction();
                break;
            case ACTIONS_OVER:
            default:
                executorService.shutdown();
                countDownLatch.countDown();
                break;
        }

        // Affichage de la carte à chaque action réaliser
        System.out.println("====== Action de " + name + " | ActionListSize = "+actionList.size()+" ========");
        System.out.println(map.displayMap());
    }

    /**
     *  Effectue l'action donnée pour un aventurier
     */
    private void doAction(){

        String action = giveNextAction();

        // Si l'action est une rotation
        if (action.equals("D") || action.equals("G")){
            rotateAdventurer(action);
            actionDone();

            // Sinon c'est l'action Avancer
        }else {
            try {
                Case newCase = giveNextForwardCase();

                // On vérifie si la nouvelle case n'est pas déjà occupé par un aventurier
                if (newCase.getState() == ECaseState.IN_USE){

                    // On vérifie si l'aventurier présent sur la case à encore des actions à faire
                    if (  ((Plaine)newCase).getAventurierPresent().getState() == ACTIONS_OVER ){
                        // Cas où l'aventurier présent à fini ses actions, auquel cas il restera sur la case tout le temps
                        // Du coup pas la peine d'attendre qu'il parte et passer à l'action suivante
                        this.state = READY_FOR_NEXT_ACTION;
                    }else{
                        // Sinon on attend que l'aventurier libère la place
                        this.state = WAITING_TO_ACT;
                    }
                }else if (newCase.getState() == ECaseState.AVAILABLE){
                    moveAdventurer(map.getCaseFromCoord(colonne, ligne), newCase);

                    // On retire l'action de la liste
                    // A noter que je n'utilise pas la méthode actionDone() car cette dernière met à jour
                    // le statut de l'aventurier, et cela pourrait écraser la mise à jour qu'effectue moveAdventurer()
                    removeFirstAction();
                }
            } catch (CantForwardException e) {
                System.out.println("Action impossible : " + e.getMessage());
                System.out.println("Passage à la prochaine action");
                actionDone();
            }
        }

        // Si la liste des actions de l'aventurier est vide, alors on le passe à l'état actions terminées
        if (actionList.isEmpty()){
            this.state = ACTIONS_OVER;
        }
    }

    /**
     * Change l'orientation de l'aventurier
     */
    public void rotateAdventurer(String direction){
        switch (this.orientation){
            case OUEST:
                if (direction.equals("D")){
                    this.orientation = EOrientation.NORD;
                }else {
                    this.orientation = EOrientation.SUD;
                }
                break;
            case NORD:
                if (direction.equals("D")){
                    this.orientation = EOrientation.EST;
                }else {
                    this.orientation = EOrientation.OUEST;
                }
                break;
            case SUD:
                if (direction.equals("D")){
                    this.orientation = EOrientation.OUEST;
                }else {
                    this.orientation = EOrientation.EST;
                }
                break;
            case EST:
                if (direction.equals("D")){
                    this.orientation = EOrientation.SUD;
                }else {
                    this.orientation = EOrientation.NORD;
                }
                break;
        }
    }

    /**
     *  Déplace l'aventurier depuis son ancienne case vers sa nouvelle case, en mettant à jours les informations
     *
     *  @param oldCase       L'ancienne case de l'aventurier
     *  @param newCase       La nouvelle case de l'aventurier
     */
    public void moveAdventurer(Case oldCase, Case newCase){

        // On renseigne les informations relatif à l'aventurier sur la nouvelle case
        ((Plaine)newCase).setAventurierPresent(this);
        newCase.setState(ECaseState.IN_USE);

        // On met à jour les informations sur l'ancienne case
        ((Plaine)oldCase).setAventurierPresent(null);
        oldCase.setState(ECaseState.AVAILABLE);

        // On met à jour les informations de l'aventurier
        this.colonne = newCase.getColonne();
        this.ligne = newCase.getLigne();

        if (((Plaine)newCase).getTresors().isEmpty()){
            this.state = EAventurierState.READY_FOR_NEXT_ACTION;
        }else {
            this.state = EAventurierState.IN_ACTION;
        }

    }

    /**
     * Donne la prochaine case où l'aventurier doit ce rendre ne fonction de son orientation
     *
     * @return                          La nouvelle case
     * @throws CantForwardException     Exception jeté lorsqu'il est impossible d'avancer pour l'aventurier
     */
    public Case giveNextForwardCase() throws CantForwardException {

        int localColonne = colonne;
        int localLigne = ligne;

        switch (this.orientation){
            case EST:
                localColonne++;
                break;
            case SUD:
                localLigne++;
                break;
            case NORD:
                localLigne--;
                break;
            case OUEST:
                localColonne--;
                break;
        }

        if (localColonne < 1 || localColonne > map.getColonne() || localLigne < 1 || localLigne > map.getLigne() ){
            throw new CantForwardException("L'aventurier ne peut pas sortir des limites de la carte");
        }

        Case newCase = map.getCaseFromCoord(localColonne,localLigne);
        if (newCase.getState() == ECaseState.FORBIDEN){
            throw new CantForwardException("L'aventurier ne peut pas franchir une montagne");
        }

        return newCase;
    }

    /**
     * Donne un tresor à l'aventurier et change son statut si c'est le dernier trésor
     *
     * @param plaine        La case où ce trouve le trésor
     */
    public void pickUpTreasor( Plaine plaine) {
        addFoundTreasure( plaine.giveOneTresor() );
        if (plaine.getTresors().isEmpty()){
            this.state = EAventurierState.READY_FOR_NEXT_ACTION;
        }
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

    private void removeFirstAction(){
        actionList = actionList.subList(1,actionList.size());
    }

    /**
     * Affiche tous les trésors trouver par l'aventurier et sa valeur total en OR
     */
    public StringBuilder displayAdventurerInfos(){
        int totalValue = 0;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n------------------------------------------------------\n");
        stringBuilder.append("Nom : ").append(name);
        stringBuilder.append("\nPosition : ").append(colonne).append("-").append(ligne);
        stringBuilder.append("\n#### Treasures found : ").append(foundTreasure.size()).append("  ####\n");
        for (Tresor t: foundTreasure ) {
            stringBuilder.append("    - ").append(t.getNom()).append("\n");
            totalValue += t.getGoldValue();
        }
        stringBuilder.append("\nTotal Value : ").append(totalValue).append(" gold\n");

        return stringBuilder;
    }
}
