package hu.fenykep.demo.exception;

import hu.fenykep.demo.error.FelhasznaloError;

public class FelhasznaloException extends RuntimeException {
    private final FelhasznaloError hiba;

    public FelhasznaloError getHiba() {
        return hiba;
    }

    public FelhasznaloException(FelhasznaloError hiba) {
        super(hiba.getHibaNev());
        this.hiba = hiba;
    }

    @Override
    public String getMessage() {
        return hiba.getHibaUzenet();
    }
}
