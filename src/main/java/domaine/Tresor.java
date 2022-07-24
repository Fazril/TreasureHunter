package domaine;

public class Tresor {

    private String nom;
    private int goldValue;

    public Tresor() {
        ETresorNames tresorNames = ETresorNames.randomTresor();
        this.nom = tresorNames.getNom();
        this.goldValue = tresorNames.getGoldValue();
    }

    public String getNom() {
        return nom;
    }

    public int getGoldValue() {
        return goldValue;
    }

    @Override
    public String toString() {
        return "Tresor{" +
                "nom='" + nom + '\'' +
                ", goldValue=" + goldValue +
                '}';
    }
}
