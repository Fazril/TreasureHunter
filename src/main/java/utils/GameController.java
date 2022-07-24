package utils;

import domaine.*;
import exceptions.CantForwardException;
import exceptions.MountainCantHaveTreasorsException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static domaine.EAventurierState.ACTIONS_OVER;
import static domaine.EAventurierState.READY_FOR_NEXT_ACTION;

public class GameController {

    private List<Aventurier> aventuriers;
    private Carte carte;

    public GameController(Integer largeur, Integer hauteur) {
        this.carte = new Carte(largeur, hauteur);
        this.aventuriers = new ArrayList<>();
    }

    /**
     * Ajoute un aventurier à la liste des aventuriers joueurs et le place sur la carte
     *
     * @param aventurier    L'aventurier à ajouter et placer
     */
    public void addAdventurer(Aventurier aventurier){
        this.aventuriers.add(aventurier);
        ((Plaine)carte.getCaseFromCoord(aventurier.getColonne(), aventurier.getLigne()) ).setAventurierPresent(aventurier);
    }

    /**
     * Place des trésors sur une case donnée
     *
     * @param colonne   L'indice de colonne de la case
     * @param ligne     L'indice de ligne de la case
     * @param tresors   Les trésors à ajouter
     * @throws MountainCantHaveTreasorsException exception
     */
    public void setTresors(Integer colonne, Integer ligne, List<Tresor> tresors) throws MountainCantHaveTreasorsException {
        Case givenCase = carte.getCaseFromCoord(colonne, ligne);
        if (givenCase instanceof Montagne){
            throw new MountainCantHaveTreasorsException("Impossible de mettre des trésors dans une montagne");
        }else{
            ((Plaine) givenCase).setTresors(tresors);
        }
    }

    /**
     *  Moteur du jeu, exécute les actions des aventuriers jusqu'a ce qu'il n'y en ai plus
     */
    public void play(){

        // On affiche l'état initiale de la carte
        carte.displayMap();

        List<Aventurier> aventuriersActionOver = new ArrayList<>();
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        // On execute la méthode toutes les secondes
        executorService.scheduleAtFixedRate(this::taskToDoEverySecond, 0, 1, TimeUnit.SECONDS);

        // On continue tant que tout les aventurier n'ont pas fini leurs actions
        while(aventuriersActionOver.size() != aventuriers.size()){

            // On met à jour la liste des aventurier qui on fini leurs actions
            aventuriersActionOver =  aventuriers.stream().filter(aventurier -> aventurier.getState() == ACTIONS_OVER )
                    .collect(Collectors.toList());
        }
        executorService.shutdown();

        System.out.println("\n========================== FIN ===============================\n");
        System.out.println("Affichage des trésors récoltés par les aventuriers");
        aventuriers.forEach(Aventurier::displayAllTreasuresFoundAndTotalValue);
    }

    /**
     *  Méthode à exécuter toutes les secondes
     *  Effectue toutes les actions des aventuriers et affiche la carte
     */
    private void taskToDoEverySecond(){

        // On réalise l'action de chaque aventurier
        aventuriers.forEach(this::handleAdventurerAction);

        // On affiche la carte suite aux déplacements/actionq des aventuriers
        carte.displayMap();
    }

    /**
     *  Prend en charge l'action de l'aventurier
     *
     *  @param aventurier    L'aventurier qui doit effectuer une action
     */
    private void handleAdventurerAction(Aventurier aventurier){

        switch (aventurier.getState()){
            case IN_ACTION:
                pickUpTreasor(aventurier, (Plaine) carte.getCaseFromCoord(aventurier.getColonne(), aventurier.getLigne()) );
                break;
            case WAITING_TO_ACT:
            case READY_FOR_NEXT_ACTION:
                String action = aventurier.giveNextAction();
                doAction(aventurier, action);
                break;
            case ACTIONS_OVER:
            default:
                break;
        }
    }

    /**
     *  Effectue l'action donnée pour un aventurier
     *
     * @param aventurier    L'aventurier qui va réaliser l'action
     * @param action        L'action à réaliser
     */
    private void doAction(Aventurier aventurier, String action){

        // Si l'action est une rotation
        if (action.equals("D") || action.equals("G")){
            rotateAdventurer(aventurier, action);
            aventurier.actionDone();

        // Sinon c'est l'action Avancer
        }else {
            try {
                Case newCase = giveNextForwardCase(aventurier);

                // On vérifie si la nouvelle case n'est pas déjà occupé par un aventurier
                if (newCase.getState() == ECaseState.IN_USE){

                    // On vérifie si l'aventurier présent sur la case à encore des actions à faire
                    if (  ((Plaine)newCase).getAventurierPresent().getState() == ACTIONS_OVER ){
                        // Cas où l'aventurier présent à fini ses actions, auquel cas il restera sur la case tout le temps
                        // Du coup pas la peine d'attendre qu'il parte et passer à l'action suivante
                        aventurier.setState(READY_FOR_NEXT_ACTION);
                    }else{
                        // Sinon on attend que l'aventurier libère la place
                        aventurier.setState(EAventurierState.WAITING_TO_ACT);
                    }
                }else if (newCase.getState() == ECaseState.AVAILABLE){
                    moveAdventurer(aventurier, carte.getCaseFromCoord(aventurier.getColonne(), aventurier.getLigne()), newCase);

                    // On retire l'action de la liste
                    // A noter que je n'utilise pas la méthode actionDone() car cette dernière met à jour
                    // le statut de l'aventurier, et cela pourrait écraser la mise à jour qu'effectue moveAdventurer()
                    aventurier.removeFirstAction();
                }
            } catch (CantForwardException e) {
                System.out.println("Action impossible : " + e.getMessage());
                System.out.println("Passage à la prochaine action");
                aventurier.actionDone();
            }
        }


        // Si la liste des actions de l'aventurier est vide, alors on le passe à l'état actions terminées
        if (aventurier.getActionList().isEmpty()){
            aventurier.setState(ACTIONS_OVER);
        }
    }

    /**
     * Change l'orientation de l'aventurier
     *
     * @param aventurier    Aventurier dont l'orientation est à changer
     */
    private void rotateAdventurer(Aventurier aventurier, String direction){
        switch (aventurier.getOrientation()){
            case OUEST:
                if (direction.equals("D")){
                    aventurier.setOrientation(EOrientation.NORD);
                }else {
                    aventurier.setOrientation(EOrientation.SUD);
                }
                break;
            case NORD:
                if (direction.equals("D")){
                    aventurier.setOrientation(EOrientation.EST);
                }else {
                    aventurier.setOrientation(EOrientation.OUEST);
                }
                break;
            case SUD:
                if (direction.equals("D")){
                    aventurier.setOrientation(EOrientation.OUEST);
                }else {
                    aventurier.setOrientation(EOrientation.EST);
                }
                break;
            case EST:
                if (direction.equals("D")){
                    aventurier.setOrientation(EOrientation.SUD);
                }else {
                    aventurier.setOrientation(EOrientation.NORD);
                }
                break;
        }
    }

    /**
     *  Déplace l'aventurier depuis son ancienne case vers sa nouvelle case, en mettant à jours les informations
     *
     *  @param aventurier    L'aventurier qui ce déplace
     *  @param oldCase       L'ancienne case de l'aventurier
     *  @param newCase       La nouvelle case de l'aventurier
     */
    public void moveAdventurer(Aventurier aventurier, Case oldCase, Case newCase){

        // On renseigne les informations relatif à l'aventurier sur la nouvelle case
        ((Plaine)newCase).setAventurierPresent(aventurier);
        newCase.setState(ECaseState.IN_USE);

        // On met à jour les informations sur l'ancienne case
        ((Plaine)oldCase).setAventurierPresent(null);
        oldCase.setState(ECaseState.AVAILABLE);

        // On met à jour les informations de l'aventurier
        aventurier.setColonne( newCase.getColonne() );
        aventurier.setLigne( newCase.getLigne() );

        if (((Plaine)newCase).getTresors().isEmpty()){
            aventurier.setState(EAventurierState.READY_FOR_NEXT_ACTION);
        }else {
            aventurier.setState(EAventurierState.IN_ACTION);
        }
    }

    /**
     * Donne la prochaine case où l'aventurier doit ce rendre ne fonction de son orientation
     *
     * @param aventurier                L'aventurier qui avance
     * @return                          La nouvelle case
     * @throws CantForwardException     Exception jeté lorsqu'il est impossible d'avancer pour l'aventurier
     */
    private Case giveNextForwardCase(Aventurier aventurier) throws CantForwardException {
        int colonne = aventurier.getColonne();
        int ligne = aventurier.getLigne();

       switch (aventurier.getOrientation()){
           case EST:
               colonne++;
               break;
           case SUD:
               ligne++;
               break;
           case NORD:
               ligne--;
               break;
           case OUEST:
               colonne--;
               break;
       }

       if (colonne < 1 || colonne == carte.getColonne() || ligne < 1 || ligne == carte.getLigne() ){
           throw new CantForwardException("L'aventurier ne peut pas sortir des limites de la carte");
       }

       Case newCase = carte.getCaseFromCoord(colonne,ligne);
       if (newCase.getState() == ECaseState.FORBIDEN){
           throw new CantForwardException("L'aventurier ne peut pas franchir une montagne");
       }

       return newCase;
    }

    /**
     * Donne un tresor à l'aventurier et change son statut si c'est le dernier trésor
     *
     * @param aventurier    L'aventurier qui ramasse le trésor
     * @param plaine        La case où ce trouve le trésor
     */
    private void pickUpTreasor(Aventurier aventurier, Plaine plaine) {
        aventurier.addFoundTreasure( plaine.giveOneTresor() );
        if (plaine.getTresors().isEmpty()){
            aventurier.setState(EAventurierState.READY_FOR_NEXT_ACTION);
        }
    }

    public Carte getCarte() {
        return carte;
    }
}
