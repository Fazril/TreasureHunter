package domaine;

public class Montagne extends Case{


    public Montagne() {
        super.state = ECaseState.FORBIDEN;
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
        return "[ T0 | M | x x ]";
    }
}
