package domaine;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum ETresorNames {

    //J'ai utilisé un générateur de nom de trésors sur internet qui me donnais aussi la valeur, du coup je l'ai rajouter

    DAGUE_DE_MAITRE("Dague de maître", 302),
    ANTIDOTE_4("Antidote (4 dose(s))", 200),
    ARBALETE_LOURDE("Arbalète lourde à répétition de maître",700),
    FAUX_DE_MAITRE("Faux de maître", 318),
    ARMURE_PLAQUE_SIZE_M("Armure à plaques de taille M",600),
    EAU_BENITE_4("Eau bénite (4 flasque(s))",100),
    BON_CADENA("Bon cadenas",80),
    LANCE_DE_MAITRE("Lance de maître",302),
    CORSEQUE_DE_MAITRE("Corsèque de maître",310),
    ARC_COURT_COMPOSITE("Arc court composite (for +1) de maître",450),
    HACHE_DE_LANCER_DE_MAITRE("Hache de lancer de maître",308),
    MATERIEL_D_ESCALADE("Matériel d'escalade",80),
    ACIDE_6("Acide (6 flasque(s))",60),
    ARC_LONG_COMPOSITE("Arc long composite (for +0) de maître",400),
    SACOCHES_IMMOBILISANTES("Sacoche(s) immobilisante(s) (2)",100),
    EPEE_LONGUE_DE_MAITRE("Epée longue de maître",315),
    HARNOIS_SIZE_M("Harnois de taille M",1500),
    INSTRUMENT_DE_MUSIQUE("Instrument de musique de maître",100),
    EXCELLENT_CADENA("Excellent cadenas",150),
    ARMURE_CUIR_CLOUTE_DE_MAITRE_SIZE_P("Armure de cuir cloutée de maître de taille P",175),
    HARNOIS_SIZE_P("Harnois de taille P",1500),
    RONDACHE_EBENITE_SIZE_M("Rondache en ébénite de taille M",203),
    TROUSSE_DEGUISEMENT("Trousse de déguisement",50),
    PIC_DE_GUERRE_LEGER_DE_MAITRE("Pic de guerre léger de maître",304),
    CIMETERRE_DE_MAITRE("Cimeterre de maître",315),
    CHEMISE_DE_MAILLE_SIZE_M("Chemise de maille de taille M",100),
    DOUBLE_LAME_DE_MAITRE("Double-lame de maître",700),
    FEU_GREGOIS_4("Feu grégois (4 flasque(s))",80),
    MENOTTES_DE_QUALITE_SUPPERIEUR("Menottes de qualité suppérieure",50),
    ECU_EBENITE_SIZE_M("Ecu en ébénite de taille M",257),
    LONGUE_VUE("Longue-vue",1000),
    LOUPE("Loupe",100),
    BATONNET_FUMIGENE_4("Bâtonnet(s) fumigène(s) (4)",80),
    NUNCHAKU_DE_MAITRE("Nunchaku de maître",302),
    CREVICE_SIZE_M("Crevice de taille M",250),
    BATONNET_FUMIGENE_1("Bâtonnet(s) fumigène(s) (1)",20),
    SYMBOLE_SACRE_ARGENT("Symbôle sacré en argent",25),
    ARMURE_CUIR_CLOUTE_DE_MAITRE_SIZE_M("Armure de cuir cloutée de maître de taille M",175),
    FEU_GREGOIS_3("Feu grégois (3 flasque(s))",60),
    OUTILS_DE_MAITRE_ARTISANT("Outils de maître artisan",55),
    ARBALETE_LEGERE_DE_MAITRE("Arbalète légère de maître",335);


    private static final List<ETresorNames> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    private String nom;
    private int goldValue;

    ETresorNames(String nom, int goldValue) {
        this.nom = nom;
        this.goldValue = goldValue;
    }

    public static ETresorNames randomTresor()  {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }

    public String getNom() {
        return nom;
    }

    public int getGoldValue() {
        return goldValue;
    }
}
