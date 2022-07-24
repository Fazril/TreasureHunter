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
}
