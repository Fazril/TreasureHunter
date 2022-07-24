package utils;

import domaine.*;
import exceptions.MountainCantHaveTreasorsException;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // Création de trésors
        List<Tresor> tresors = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            tresors.add(new Tresor());
        }


        GameController gameController = new GameController(6,5);

        // Ajout d'une montagne sur le chemin pour tester l'évitement de l'aventurier
        gameController.getCarte().setCaseFromCoord(3,1, new Montagne());

        // On ajoute les trésors à la carte
        try {
            gameController.setTresors(2,2, tresors);
        } catch (MountainCantHaveTreasorsException e) {
            System.out.println(e.getMessage());
        }

        Aventurier john = new Aventurier("John", 1, 1, EOrientation.EST, "AADADAGA");
        gameController.addAdventurer(john);

        System.out.println("======== Game Start =========\n");
        gameController.play();
        System.out.println("\n======== Game End =========");

    }
}
