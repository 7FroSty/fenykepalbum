CREATE OR REPLACE PROCEDURE FelhasznaloRegisztracio (
    p_nev IN Felhasznalo.nev%TYPE,
    p_email IN Felhasznalo.email%TYPE,
    p_jelszo IN Felhasznalo.jelszo%TYPE,
    p_iranyitoszam IN Felhasznalo.iranyitoszam%TYPE,
    p_telepules IN Felhasznalo.telepules%TYPE,
    p_utca IN Felhasznalo.utca%TYPE,
    p_hazszam IN Felhasznalo.hazszam%TYPE,
    p_error OUT NUMBER
    -- Errors:
    -- 0: No error
    -- 1: Duplicate email error
    -- 10: Other error
) AS 
BEGIN
    p_error := 0;
    INSERT INTO Felhasznalo(nev, email, jelszo, iranyitoszam, telepules, utca, hazszam)
        VALUES(p_nev, p_email, p_jelszo, p_iranyitoszam, p_telepules, p_utca, p_hazszam);
    
EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        p_error := 1;
    WHEN OTHERS THEN
        p_error := 10;
END;
/


CREATE OR REPLACE PROCEDURE KepFeltoltes (
    p_felhasznalo_id IN Felhasznalo.id%TYPE,
    p_kategoria_id IN Kategoria.id%TYPE,
    p_cim IN Kep.cim%TYPE,
    p_tartalom IN Kep.tartalom%TYPE,
    p_idopont IN Kep.idopont%TYPE,
    p_telepules IN Kep.telepules%TYPE,
    p_error OUT NUMBER
    -- OUT_ERROR:
    -- 0: No error
    -- 1: Duplicate email error
    -- 10: Other error
) AS 
BEGIN
    p_error := 0;
    INSERT INTO Kep(felhasznalo_id, kategoria_id, cim, tartalom, idopont, telepules)
        VALUES(p_felhasznalo_id, p_kategoria_id, p_cim, p_tartalom, p_idopont, p_telepules);

END;
/


CREATE OR REPLACE FUNCTION Hasonlo (
    p_szoveg IN VARCHAR2, -- Ebben keresünk a p_keresendo szövegben lévő szavakkal hasonlóságot
    p_keresendo IN VARCHAR2,
    p_tolerancia IN NUMBER -- 0 - Kicsi, 1 - Nagy
)
RETURN NUMBER -- Ha az egyik szó hasonlít a p_szoveg-ben lévő egyik szóra, akkor 1(igaz)
AS
    v_szoveg VARCHAR2(1000);
    v_keresendo VARCHAR2(1000);
    
    p_szoveg_szavak NUMBER;
    p_keresendo_szavak NUMBER;
    
    v_tolarencia NUMBER;
BEGIN
    v_tolarencia := 85; -- Kicsi
    IF p_tolerancia = 1 THEN
        v_tolarencia := 60; -- Nagy
    END IF;

    -- Betűk ASCII kódúra konvertálása
    SELECT CONVERT(p_szoveg, 'US7ASCII') INTO v_szoveg FROM DUAL;
    SELECT CONVERT(p_keresendo, 'US7ASCII') INTO v_keresendo FROM DUAL;

    -- Szavak megszámlálása
    SELECT REGEXP_COUNT(v_szoveg, '\w+') INTO p_szoveg_szavak FROM dual;
    SELECT REGEXP_COUNT(v_keresendo, '\w+') INTO p_keresendo_szavak FROM dual;

    FOR i IN 1..p_szoveg_szavak LOOP
            FOR j IN 1..p_keresendo_szavak LOOP
                    IF UTL_MATCH.JARO_WINKLER_SIMILARITY(
                               REGEXP_SUBSTR(LOWER(v_szoveg), '\w+', 1, i),
                               REGEXP_SUBSTR(LOWER(v_keresendo), '\w+', 1, j)) >= v_tolarencia
                    OR
                        LOWER(v_szoveg) LIKE '%' || LOWER(v_keresendo) || '%'
                    THEN
                        RETURN 1;
                    END IF;
                END LOOP;
        END LOOP;

    RETURN 0;
END;
/


CREATE OR REPLACE PROCEDURE KepKeresesCim (
    p_cim IN VARCHAR2,

    /* 0 - Legújabb elől
     * 1 - Legrégebbi elől
     */
    p_rendez IN NUMBER,

    /* 0 - Kis tolarencia
     * 1 - Nagy tolarencia
     */
    p_tolarencia IN NUMBER,
    c_kepek OUT SYS_REFCURSOR
)
AS
BEGIN

    IF p_rendez = 1 THEN
        OPEN c_kepek FOR
            SELECT * FROM Kep WHERE HASONLO(cim, p_cim, p_tolarencia) = 1 ORDER BY idopont;
    ELSE
        OPEN c_kepek FOR
            SELECT * FROM Kep WHERE HASONLO(cim, p_cim, p_tolarencia) = 1 ORDER BY idopont DESC;
    END IF;
END;
/


CREATE OR REPLACE PROCEDURE KepKeresesFelhasznalo (
    p_nev IN VARCHAR2,

    /* 0 - Legújabb elől
     * 1 - Legrégebbi elől
     */
    p_rendez IN NUMBER,

    /* 0 - Kis tolarencia
     * 1 - Nagy tolarencia
     */
    p_tolarencia IN NUMBER,
    c_kepek OUT SYS_REFCURSOR
)
AS
BEGIN

    IF p_rendez = 1 THEN
        OPEN c_kepek FOR
            SELECT Kep.* FROM Kep
                LEFT JOIN Felhasznalo ON Kep.felhasznalo_id = Felhasznalo.id
            WHERE HASONLO(Felhasznalo.nev, p_nev, p_tolarencia) = 1
            ORDER BY idopont;
    ELSE
        OPEN c_kepek FOR
            SELECT Kep.* FROM Kep
                LEFT JOIN Felhasznalo ON Kep.felhasznalo_id = Felhasznalo.id
            WHERE HASONLO(Felhasznalo.nev, p_nev, p_tolarencia) = 1
            ORDER BY idopont DESC;
    END IF;
END;
/


CREATE OR REPLACE PROCEDURE KepKeresesKategoria (
    p_kategoria IN VARCHAR2,

    /* 0 - Legújabb elől
     * 1 - Legrégebbi elől
     */
    p_rendez IN NUMBER,

    /* 0 - Kis tolarencia
     * 1 - Nagy tolarencia
     */
    p_tolarencia IN NUMBER,
    c_kepek OUT SYS_REFCURSOR
)
AS
BEGIN
    IF p_rendez = 1 THEN
        OPEN c_kepek FOR
            SELECT Kep.* FROM Kep
                LEFT JOIN Kategoria ON Kep.kategoria_id = Kategoria.id
            WHERE HASONLO(Kategoria.nev, p_kategoria, p_tolarencia) = 1
            ORDER BY idopont;
    ELSE
        OPEN c_kepek FOR
            SELECT * FROM Kep
                LEFT JOIN Kategoria ON Kep.kategoria_id = Kategoria.id
            WHERE HASONLO(Kategoria.nev, p_kategoria, p_tolarencia) = 1
            ORDER BY idopont DESC;
    END IF;
END;
/


CREATE OR REPLACE PROCEDURE KepKeresesKulcsszo (
    p_kulcsszo IN VARCHAR2,

    /* 0 - Legújabb elől
     * 1 - Legrégebbi elől
     */
    p_rendez IN NUMBER,

    /* 0 - Kis tolarencia
     * 1 - Nagy tolarencia
     */
    p_tolarencia IN NUMBER,
    c_kepek OUT SYS_REFCURSOR
)
AS
BEGIN

    IF p_rendez = 1 THEN
        OPEN c_kepek FOR
            SELECT Kep.* FROM Kep
                LEFT JOIN KepKulcsszo on Kep.id = KepKulcsszo.kep_id
                LEFT JOIN Kulcsszo on Kulcsszo.id = KepKulcsszo.kulcsszo_id
            WHERE HASONLO(Kulcsszo.nev, p_kulcsszo, p_tolarencia) = 1
            ORDER BY idopont;
    ELSE
        OPEN c_kepek FOR
            SELECT Kep.* FROM Kep
                LEFT JOIN KepKulcsszo on Kep.id = KepKulcsszo.kep_id
                LEFT JOIN Kulcsszo on Kulcsszo.id = KepKulcsszo.kulcsszo_id
            WHERE HASONLO(Kulcsszo.nev, p_kulcsszo, p_tolarencia) = 1
            ORDER BY idopont DESC;
    END IF;
END;
/


CREATE OR REPLACE PROCEDURE KepKeresesTelepules (
    p_telepules IN VARCHAR2,

    /* 0 - Legújabb elől
     * 1 - Legrégebbi elől
     */
    p_rendez IN NUMBER,

    /* 0 - Kis tolarencia
     * 1 - Nagy tolarencia
     */
    p_tolarencia IN NUMBER,
    c_kepek OUT SYS_REFCURSOR
)
AS
BEGIN

    IF p_rendez = 1 THEN
        OPEN c_kepek FOR
            SELECT Kep.* FROM Kep
                WHERE HASONLO(telepules, p_telepules, p_tolarencia) = 1
            ORDER BY idopont;
    ELSE
        OPEN c_kepek FOR
            SELECT Kep.* FROM Kep
                WHERE HASONLO(telepules, p_telepules, p_tolarencia) = 1
            ORDER BY idopont DESC;
    END IF;
END;
/


CREATE OR REPLACE PROCEDURE KepErtekeles (
    p_felhasznalo_id IN NUMBER,
    p_kep_id IN NUMBER,
    p_csillagok IN NUMBER
)
AS
BEGIN
    IF p_csillagok < 0 OR p_csillagok > 5 THEN
        RAISE_APPLICATION_ERROR(-20100, 'Hibás csillag mennyiség');
    ELSIF p_csillagok = 0 THEN
        DELETE FROM Ertekeles WHERE kep_id = p_kep_id AND felhasznalo_id = p_felhasznalo_id;
    ELSE
        INSERT INTO Ertekeles(felhasznalo_id, kep_id, csillagok) VALUES(p_felhasznalo_id, p_kep_id, p_csillagok);
    END IF;

EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        UPDATE Ertekeles SET csillagok = p_csillagok WHERE felhasznalo_id = p_felhasznalo_id AND kep_id = p_kep_id;
END;
/


CREATE OR REPLACE PROCEDURE KATFELTOLTES (
    p_kat_nev IN Kategoria.nev%TYPE
) AS
BEGIN
    INSERT INTO Kategoria(nev)
        VALUES(p_kat_nev);
END;
/


CREATE OR REPLACE PROCEDURE KATFRISSITES (
    p_kat_nev IN Kategoria.nev%TYPE,
    p_kat_id IN kategoria.id%TYPE
) AS
BEGIN
    UPDATE kategoria
        SET nev = p_kat_nev
        WHERE id = p_kat_id;
END;
/


CREATE OR REPLACE PROCEDURE KATTORLES (
    p_kat_id IN kategoria.id%TYPE
) AS
BEGIN
    DELETE
    FROM
        kategoria
    WHERE
        id = p_kat_id;
END;
/


CREATE OR REPLACE PROCEDURE AktivVersenyek (
    p_datum IN DATE,

    c_versenyek OUT SYS_REFCURSOR
)
AS
BEGIN
    OPEN c_versenyek FOR
        SELECT * FROM Verseny WHERE szavazas_kezdete <= TO_DATE(p_datum) AND szavazas_vege >= TO_DATE(p_datum);
END;
/


CREATE OR REPLACE PROCEDURE BEJEGYZESFELTOLTES (
    p_cim IN bejegyzes.cim%TYPE,
    p_tartalom IN bejegyzes.szoveg%TYPE,
    p_id IN bejegyzes.felhasznalo_id%TYPE
) AS
BEGIN
    INSERT INTO Bejegyzes(cim, szoveg, felhasznalo_id)
        VALUES(p_cim, p_tartalom, p_id);
END;
/


CREATE OR REPLACE PROCEDURE BEJEGYZESMODOSITAS (
    p_cim IN bejegyzes.cim%TYPE,
    p_szoveg IN bejegyzes.szoveg%TYPE,
    p_id IN bejegyzes.felhasznalo_id%TYPE
) AS
BEGIN
    UPDATE Bejegyzes
    SET cim = p_cim,
        szoveg = p_szoveg
    WHERE
        id = p_id;
END;
/


CREATE OR REPLACE PROCEDURE KOMMENTFELTOLTES (
    p_szoveg IN komment.szoveg%TYPE,
    p_kep_id IN komment.kep_id%TYPE,
    p_felhasznalo_id IN komment.felhasznalo_id%TYPE
) AS
BEGIN
    INSERT INTO Komment(szoveg, kep_id, felhasznalo_id)
        VALUES(p_szoveg, p_kep_id, p_felhasznalo_id);
END;
/
