package domaine;

import exceptions.CantForwardException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AventurierTest {

    @Test
    void rotateAdventurer() {
        Aventurier aventurier = new Aventurier("TestMan",1,1, EOrientation.EST,"A" );

        //On test toutes les rotations

        // EST - Droite
        aventurier.rotateAdventurer("D");
        assertEquals(EOrientation.SUD, aventurier.getOrientation());

        // EST - Gauche
        aventurier.setOrientation(EOrientation.EST);
        aventurier.rotateAdventurer("G");
        assertEquals(EOrientation.NORD, aventurier.getOrientation());

        // NORD - Droite
        aventurier.setOrientation(EOrientation.NORD);
        aventurier.rotateAdventurer("D");
        assertEquals(EOrientation.EST, aventurier.getOrientation());

        // NORD - Gauche
        aventurier.setOrientation(EOrientation.NORD);
        aventurier.rotateAdventurer("G");
        assertEquals(EOrientation.OUEST, aventurier.getOrientation());

        // OUEST - Droite
        aventurier.setOrientation(EOrientation.OUEST);
        aventurier.rotateAdventurer("D");
        assertEquals(EOrientation.NORD, aventurier.getOrientation());

        // OUEST - Gauche
        aventurier.setOrientation(EOrientation.OUEST);
        aventurier.rotateAdventurer("G");
        assertEquals(EOrientation.SUD, aventurier.getOrientation());

        // SUD - Droite
        aventurier.setOrientation(EOrientation.SUD);
        aventurier.rotateAdventurer("D");
        assertEquals(EOrientation.OUEST, aventurier.getOrientation());

        // SUD - Gauche
        aventurier.setOrientation(EOrientation.SUD);
        aventurier.rotateAdventurer("G");
        assertEquals(EOrientation.EST, aventurier.getOrientation());
    }

    @Test
    void moveAdventurer() {
        Aventurier aventurier = new Aventurier("TestMan",1,1, EOrientation.EST,"A" );
        Plaine plaineOld = new Plaine(1,1);
        Plaine plaineNew = new Plaine(1,2);
        Tresor tresor = new Tresor();
        plaineNew.setTresors(Collections.singletonList(tresor));

        plaineOld.setAventurierPresent(aventurier);

        assertNull(plaineNew.getAventurierPresent());
        assertEquals(aventurier,plaineOld.getAventurierPresent());
        assertEquals(plaineNew.getState(), ECaseState.AVAILABLE);
        assertEquals(aventurier.getState(), EAventurierState.READY_FOR_NEXT_ACTION);

        aventurier.moveAdventurer(plaineOld,plaineNew);

        assertNull(plaineOld.getAventurierPresent());
        assertEquals(aventurier,plaineNew.getAventurierPresent());
        assertEquals(plaineNew.getColonne(), aventurier.getColonne());
        assertEquals(plaineNew.getLigne(), aventurier.getLigne());
        assertEquals(plaineNew.getState(), ECaseState.IN_USE);
        assertEquals(plaineOld.getState(), ECaseState.AVAILABLE);
        assertEquals(aventurier.getState(),EAventurierState.IN_ACTION);
    }

    @Test
    void giveNextForwardCase() throws CantForwardException {
        Aventurier aventurier = new Aventurier("TestMan",1,1, EOrientation.NORD,"A" );
        Carte carte = Carte.getInstance(2,2);
        carte.setCaseFromCoord(1,2, new Montagne());
        aventurier.setMap(carte);

        // Test sortir des limites
        CantForwardException exception = assertThrows(CantForwardException.class , aventurier::giveNextForwardCase);
        assertEquals("L'aventurier ne peut pas sortir des limites de la carte", exception.getMessage());

        // Test avancer sur une Montagne
        aventurier.setOrientation(EOrientation.SUD);
        CantForwardException exception2 = assertThrows(CantForwardException.class , aventurier::giveNextForwardCase);
        assertEquals("L'aventurier ne peut pas franchir une montagne", exception2.getMessage());

        // Test avancer sur plaine
        aventurier.setOrientation(EOrientation.EST);
        Case nextForwardCase = aventurier.giveNextForwardCase();
        assertEquals(2, nextForwardCase.getColonne());
        assertEquals(1, nextForwardCase.getLigne());
    }

    @Test
    void pickUpTreasor() {
        Aventurier aventurier = new Aventurier("TestMan",1,1, EOrientation.EST,"A" );
        Carte carte = Carte.getInstance(2,2);
        Tresor tresor = new Tresor();
        List<Tresor> tresorList = new ArrayList<>();
        tresorList.add(tresor);

        ((Plaine) carte.getCaseFromCoord(1,2) ).setTresors(tresorList);

        Case oldCase = carte.getCaseFromCoord(1,1);
        Case newCase = carte.getCaseFromCoord(1,2);
        aventurier.moveAdventurer( oldCase, newCase );

        assertEquals(1, aventurier.getColonne());
        assertEquals(2, aventurier.getLigne());
        assertEquals(EAventurierState.IN_ACTION, aventurier.getState());

        assertEquals(0, aventurier.getFoundTreasure().size());

        Plaine plaineWithTreasor = (Plaine) carte.getCaseFromCoord(1,2);
        aventurier.pickUpTreasor( plaineWithTreasor );

        assertEquals(0, plaineWithTreasor.getTresors().size());
        assertEquals(1, aventurier.getFoundTreasure().size());
        assertEquals(EAventurierState.READY_FOR_NEXT_ACTION, aventurier.getState());
    }

    @Test
    void giveNextActionAndActionDone() {
        Aventurier aventurier = new Aventurier("TestMan",1,1, EOrientation.EST,"ADG" );
        aventurier.setState(EAventurierState.IN_ACTION);

        assertEquals(3, aventurier.getActionList().size());
        assertEquals("A", aventurier.giveNextAction());
        aventurier.actionDone();
        assertEquals(2, aventurier.getActionList().size());
        assertEquals( EAventurierState.READY_FOR_NEXT_ACTION, aventurier.getState());
    }

}