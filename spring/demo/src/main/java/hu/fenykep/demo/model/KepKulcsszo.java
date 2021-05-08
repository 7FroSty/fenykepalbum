package hu.fenykep.demo.model;

public class KepKulcsszo {
    private int kep_id;
    private int kulcsszo_id;

    public KepKulcsszo(int kep_id, int kulcsszo_id) {
        this.kep_id=kep_id;
        this.kulcsszo_id=kulcsszo_id;
    }

    public int getKep_id() {
        return kep_id;
    }

    public void setKep_id(int kep_id) {
        this.kep_id = kep_id;
    }

    public int getKulcsszo_id() {
        return kulcsszo_id;
    }

    public void setKulcsszo_id(int kulcsszo_id) {
        this.kulcsszo_id = kulcsszo_id;
    }
}
