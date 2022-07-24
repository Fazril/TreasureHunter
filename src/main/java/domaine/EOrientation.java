package domaine;

public enum EOrientation {

    NORD("N"),
    OUEST("O"),
    EST("E"),
    SUD("S");

    String initiale;

    EOrientation(String initiale) {
        this.initiale = initiale;
    }

    public String getInitiale() {
        return initiale;
    }

    public static EOrientation retrieveByInitial(String n) {
        switch (n) {
            case "N":
                return EOrientation.NORD;
            case "O":
                return EOrientation.OUEST;
            case "E":
                return EOrientation.EST;
            case "S":
                return EOrientation.SUD;
            default:
                return null;
        }
    }
}
