package hu.fenykep.demo.error;

public enum FelhasznaloError {
    // 0 - nincs hiba
    // SQL hibák
    FOGLALT_EMAIL("FOGLALT_EMAIL", 1, "Ez az Email cím már foglalt."),
    EGYEB_HIBA("EGYEB_HIBA", 10, "Egyéb hiba."),

    // Regisztrációs hibák
    REG_BEVITEL_NEV("REG_BEVITEL_NEV", 11, "A név hossza 3-50 karakter."),
    REG_BEVITEL_EMAIL("REG_BEVITEL_EMAIL", 12, "Hibás Email formátum"),
    REG_BEVITEL_JELSZO("REG_BEVITEL_JELSZO", 13, "A jelszó hossza 8-50 karakter és tartalmaznia kell kis és nagybetűt, illetve számot egyaránt."),
    REG_BEVITEL_IRANYITOSZAM("REG_BEVITEL_IRANYITOSZAM", 14, "Az irányítószám 0 és 9999 közötti számból áll."),
    REG_BEVITEL_TELEPULES("REG_BEVITEL_TELEPULES", 15, "A település maximum 50 karakterből állhat."),
    REG_BEVITEL_UTCA("REG_BEVITEL_UTCA", 16, "Az utca maximum 50 karakterből állhat."),
    REG_BEVITEL_HAZSZAM("REG_BEVITEL_HAZSZAM", 17, "A házszám maximum 50 karakterből állhat."),

    // Bejelentkezés hibák
    LOG_BEVITEL_EMAILJELSZO("LOG_BEVITEL_EMAILJELSZO", 20, "Hibás Email cím vagy jelszó.");


    private final String hibaNev;
    private final int hibaKod;
    private final String hibaUzenet;

    public String getHibaNev() {
        return hibaNev;
    }

    public int getHibaKod() {
        return hibaKod;
    }

    public String getHibaUzenet() {
        return hibaUzenet;
    }

    FelhasznaloError(String hibaNev, int hibaKod, String hibaUzenet) {
        this.hibaNev = hibaNev;
        this.hibaKod = hibaKod;
        this.hibaUzenet = hibaUzenet;
    }

    public static FelhasznaloError toFelhasznaloError(int hibaKod) {
        if(hibaKod == 0) {
            return null;
        }
        FelhasznaloError ret = null;

        FelhasznaloError[] hibak = values();

        for (FelhasznaloError hiba : hibak) {
            if (hiba.getHibaKod() == hibaKod) {
                ret = hiba;
                break;
            }
        }
        return ret;
    }
}
