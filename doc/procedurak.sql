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
                REGEXP_SUBSTR(LOWER(v_keresendo), '\w+', 1, j)) >= v_tolarencia THEN
                RETURN 1;
            END IF;
        END LOOP;
    END LOOP;

    RETURN 0;
END;
/


CREATE OR REPLACE PROCEDURE KepListaKategoria (
    /* 0 - Minden kép
     * 1.. - Kategoria ID
     */
    p_kategoria IN NUMBER DEFAULT 0,
    
    /* 0 - Legújabb elől
     * 1 - Legrégebbi elől
     */
    p_rendez IN NUMBER DEFAULT 0,
    c_kepek OUT SYS_REFCURSOR
)
AS
BEGIN
    IF p_kategoria = 0 THEN
        IF p_rendez = 1 THEN
            OPEN c_kepek FOR SELECT * FROM Kep ORDER BY idopont;
        ELSE
            OPEN c_kepek FOR SELECT * FROM Kep ORDER BY idopont DESC;
        END IF;
    ELSE
        IF p_rendez = 1 THEN
            OPEN c_kepek FOR
                SELECT * FROM Kep WHERE kategoria_id = p_kategoria ORDER BY idopont;
        ELSE
            OPEN c_kepek FOR
                SELECT * FROM Kep WHERE kategoria_id = p_kategoria ORDER BY idopont DESC;
        END IF;
    END IF;
END;
/


CREATE OR REPLACE PROCEDURE KepListaCim (
    p_cim IN VARCHAR2,

    /* 0 - Legújabb elől
     * 1 - Legrégebbi elől
     */
    p_rendez IN NUMBER DEFAULT 0,
    c_kepek OUT SYS_REFCURSOR
)
AS
BEGIN

    IF p_rendez = 1 THEN
        OPEN c_kepek FOR
            SELECT * FROM Kep WHERE HASONLO(cim, p_cim, 0) = 1 ORDER BY idopont;
    ELSE
        OPEN c_kepek FOR
            SELECT * FROM Kep WHERE HASONLO(cim, p_cim, 0) = 1 ORDER BY idopont DESC;
    END IF;
END;
/


CREATE OR REPLACE PROCEDURE KepListaKulcsszo (
    p_kulcsszo IN VARCHAR2,

    /* 0 - Legújabb elől
     * 1 - Legrégebbi elől
     */
    p_rendez IN NUMBER DEFAULT 0,
    c_kepek OUT SYS_REFCURSOR
)
AS
BEGIN

    IF p_rendez = 1 THEN
        OPEN c_kepek FOR
            SELECT * FROM Kep
                LEFT JOIN KepKulcsszo kk on Kep.id = kk.kep_id
                LEFT JOIN KULCSSZO kszo on kszo.id = kk.kulcsszo_id
                WHERE HASONLO(kszo.nev, p_kulcsszo, 0) = 1
                ORDER BY idopont;
    ELSE
        OPEN c_kepek FOR
            SELECT * FROM Kep
                LEFT JOIN KepKulcsszo kk on Kep.id = kk.kep_id
                LEFT JOIN KULCSSZO kszo on kszo.id = kk.kulcsszo_id
                WHERE HASONLO(kszo.nev, p_kulcsszo, 0) = 1
                ORDER BY idopont DESC;
    END IF;
END;
/


CREATE OR REPLACE PROCEDURE KepListaTelepules (
    p_telepules IN VARCHAR2,

    /* 0 - Legújabb elől
     * 1 - Legrégebbi elől
     */
    p_rendez IN NUMBER DEFAULT 0,
    c_kepek OUT SYS_REFCURSOR
)
AS
BEGIN

    IF p_rendez = 1 THEN
        OPEN c_kepek FOR
            SELECT * FROM Kep
            WHERE HASONLO(telepules, p_telepules, 0) = 1
            ORDER BY idopont;
    ELSE
        OPEN c_kepek FOR
            SELECT * FROM Kep
            WHERE HASONLO(telepules, p_telepules, 0) = 1
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