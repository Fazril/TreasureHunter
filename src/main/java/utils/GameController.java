package utils;

import domaine.*;
import exceptions.CantForwardException;
import exceptions.MountainCantHaveTreasorsException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static domaine.EAventurierState.ACTIONS_OVER;
import static domaine.EAventurierState.READY_FOR_NEXT_ACTION;

public class GameController {

    private List<Aventurier> aventuriers;
    private Carte carte;

    public GameController(Integer largeur, Integer hauteur) {
        this.carte = Carte.getInstance(largeur, hauteur);
        this.aventuriers = new ArrayList<>();
    }

    /**
     * Constructeur prennant en entrée des fichiers text
     *
     * @param map           Fichier pour la carte
     * @param aventuriers   Fichier pour les aventuriers
     */
    public GameController(File map, File aventuriers) {

        this.aventuriers = new ArrayList<>();

        // Lecture du fichier de la carte
        try(BufferedReader br = new BufferedReader(new FileReader(map))){
            String line;
            while ((line = br.readLine()) != null) {
                String[] splittedLine = line.split(" ");

                switch (splittedLine[0]){
                    // Si première Lettre est C alors on créer la carte avec les informations qui suis
                    case "C":
                        Integer largeur = Integer.parseInt(splittedLine[1]);
                        Integer hauteur = Integer.parseInt(splittedLine[2]);
                        this.carte = Carte.getInstance(largeur, hauteur);
                        break;
                    case "T":
                        int nbTresor = Integer.parseInt(splittedLine[2]);
                        String[] coordT = splittedLine[1].split("-");
                        Integer colonneT = Integer.parseInt(coordT[0]);
                        Integer ligneT = Integer.parseInt(coordT[1]);

                        // Création de trésors
                        List<Tresor> tresors = new ArrayList<>();
                        for (int i = 0; i < nbTresor; i++) {
                            tresors.add(new Tresor());
                        }

                        // On ajoute les trésors à la carte
                        try {
                            setTresors(colonneT,ligneT, tresors);
                        } catch (MountainCantHaveTreasorsException e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case "M":
                        String[] coordM = splittedLine[1].split("-");
                        int colonneM = Integer.parseInt(coordM[0]);
                        int ligneM = Integer.parseInt(coordM[1]);

                        carte.setCaseFromCoord(colonneM, ligneM, new Montagne());
                        break;
                    default:
                        System.out.println("Lettre non reconnu. Lettre reconnu -> 'C', 'T', 'M'");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Lecture du fichier des aventuriers
        try(BufferedReader br = new BufferedReader(new FileReader(aventuriers))){
            String line;
            while ((line = br.readLine()) != null) {
                String[] splittedLine = line.split(" ");

                String nom = splittedLine[0];
                String[] coordA = splittedLine[1].split("-");
                Integer colonneA = Integer.parseInt(coordA[0]);
                Integer ligneA = Integer.parseInt(coordA[1]);
                EOrientation orientation = EOrientation.retrieveByInitial(splittedLine[2]);
                String actions = splittedLine[3];

                // Création de l'aventurier
                Aventurier aventurier = new Aventurier(nom,colonneA, ligneA, orientation,actions);

                // ajout de l'aventurier au jeu
                addAdventurer(aventurier);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Ajoute un aventurier à la liste des aventuriers joueurs et le place sur la carte
     *
     * @param aventurier    L'aventurier à ajouter et placer
     */
    public void addAdventurer(Aventurier aventurier){
        this.aventuriers.add(aventurier);
        aventurier.setMap(carte);
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
        System.out.println(carte.displayMap().toString());

        // Permet de compter les Thread terminer
        CountDownLatch countDownLatch = new CountDownLatch(aventuriers.size());

        // On lance un Thread par aventurier
        for (Aventurier a : aventuriers ) {

            Thread newThread = new Thread(() -> a.beginAdventure(countDownLatch));
            newThread.start();
        }

        try {
            // On attend que tous les Threads soient terminer
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n========================== FIN ===============================\n");

        // Ecriture dans le fichier en find de partie
        try {
            writeToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ecrit dans un fichier de sortie l'état de la carte ainsi que l'inventaire des aventuriers
     *
     * @throws IOException  Exception relatif à l'écriture
     */
    private void writeToFile() throws IOException {

        File outputFile = new File("src/main/resources/outputFiles/output.txt");
        if (outputFile.createNewFile()){
            System.out.println("Fichier créer : " + outputFile.getName());
        }else {
            System.out.println("Le fichier existe déjà");
        }

        FileWriter fileWriter = new FileWriter(outputFile);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.printf("Etat de la carte en fin de partie :\n");
        printWriter.printf(carte.displayMap().toString());
        printWriter.printf("\nAffichage des informations sur les aventuriers");
        aventuriers.forEach(Aventurier -> printWriter.printf(Aventurier.displayAdventurerInfos().toString()));
        printWriter.close();
    }

    public Carte getCarte() {
        return carte;
    }
}
