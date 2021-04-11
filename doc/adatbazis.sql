DROP TABLE Szavazat;
DROP TABLE Nevezes;
DROP TABLE Verseny;
DROP TABLE Bejegyzes;
DROP TABLE KepMegtekintes;
DROP TABLE Komment;
DROP TABLE Ertekeles;
DROP TABLE KepKulcsszo;
DROP TABLE Kep;
DROP TABLE Kulcsszo;
DROP TABLE Kategoria;
DROP TABLE Felhasznalo;

DROP SEQUENCE felhasznalo_seq;
DROP SEQUENCE kategoria_seq;
DROP SEQUENCE kulcsszo_seq;
DROP SEQUENCE kep_seq;
DROP SEQUENCE bejegyzes_seq;
DROP SEQUENCE verseny_seq;
DROP SEQUENCE nevezes_seq;


CREATE SEQUENCE felhasznalo_seq START WITH 1000;
CREATE SEQUENCE kategoria_seq START WITH 1000;
CREATE SEQUENCE kulcsszo_seq START WITH 1000;
CREATE SEQUENCE kep_seq START WITH 1000;
CREATE SEQUENCE bejegyzes_seq START WITH 1000;
CREATE SEQUENCE verseny_seq START WITH 1000;
CREATE SEQUENCE nevezes_seq START WITH 1000;

CREATE TABLE Felhasznalo(
    id NUMBER(10) DEFAULT felhasznalo_seq.NEXTVAL NOT NULL,
    nev VARCHAR2(50) NOT NULL,
    email VARCHAR2(50) NOT NULL,
    jelszo VARCHAR2(256) NOT NULL,
    iranyitoszam NUMBER(4) NOT NULL,
    telepules VARCHAR2(50) NOT NULL,
    utca VARCHAR2(50) NOT NULL,
    hazszam VARCHAR2(50) NOT NULL,
    admin NUMBER(1) DEFAULT 0 NOT NULL,
    CONSTRAINT felhasznalo_pk_id PRIMARY KEY(id),
    CONSTRAINT felhasznalo_u_email UNIQUE(email)
);

CREATE TABLE Kategoria(
    id NUMBER(10) DEFAULT kategoria_seq.NEXTVAL NOT NULL,
    nev VARCHAR2(50) NOT NULL,
    CONSTRAINT kategoria_pk_id PRIMARY KEY(id),
    CONSTRAINT kategoria_u_nev UNIQUE(nev)
);

CREATE TABLE Kulcsszo(
    id NUMBER(10) DEFAULT kulcsszo_seq.NEXTVAL NOT NULL,
    nev VARCHAR2(50) NOT NULL,
    CONSTRAINT kulcsszo_pk_id PRIMARY KEY(id)
);

CREATE TABLE Kep(
    id NUMBER(10) DEFAULT kep_seq.NEXTVAL NOT NULL,
    felhasznalo_id NUMBER(10) NOT NULL,
    kategoria_id NUMBER(10) NOT NULL,
    cim VARCHAR2(50) NOT NULL,
    tartalom CLOB NOT NULL,
    idopont TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    telepules VARCHAR2(50),
    CONSTRAINT kep_pk_id PRIMARY KEY(id),
    CONSTRAINT kep_fk_felhasznalo_id FOREIGN KEY(felhasznalo_id) REFERENCES Felhasznalo(id) ON DELETE CASCADE,
    CONSTRAINT kep_fk_kategoria_id FOREIGN KEY(kategoria_id) REFERENCES Kategoria(id) ON DELETE CASCADE
);

CREATE TABLE KepKulcsszo(
    kep_id NUMBER(10) NOT NULL,
    kulcsszo_id NUMBER(10) NOT NULL,
    CONSTRAINT kepkulcsszo_pk PRIMARY KEY(kep_id, kulcsszo_id),
    CONSTRAINT kepkulcsszo_fk_kep_id FOREIGN KEY(kep_id) REFERENCES Kep(id) ON DELETE CASCADE,
    CONSTRAINT kepkulcsszo_fk_kulcsszo_id FOREIGN KEY(kulcsszo_id) REFERENCES Kulcsszo(id) ON DELETE CASCADE
);

CREATE TABLE Ertekeles(
    felhasznalo_id NUMBER(10) NOT NULL,
    kep_id NUMBER(10) NOT NULL,
    csillagok NUMBER(1) NOT NULL,
    CONSTRAINT ertekeles_pk PRIMARY KEY(felhasznalo_id, kep_id),
    CONSTRAINT ertekeles_fk_felhasznalo_id FOREIGN KEY(felhasznalo_id) REFERENCES Felhasznalo(id) ON DELETE CASCADE,
    CONSTRAINT ertekeles_fk_kep_id FOREIGN KEY(kep_id) REFERENCES Kep(id) ON DELETE CASCADE
);

CREATE TABLE Komment(
    felhasznalo_id NUMBER(10) NOT NULL,
    kep_id NUMBER(10) NOT NULL,
    szoveg VARCHAR2(1000) NOT NULL,
    idopont TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT komment_pk PRIMARY KEY(felhasznalo_id, kep_id),
    CONSTRAINT komment_fk_felhasznalo_id FOREIGN KEY(felhasznalo_id) REFERENCES Felhasznalo(id) ON DELETE CASCADE,
    CONSTRAINT komment_fk_kep_id FOREIGN KEY(kep_id) REFERENCES Kep(id) ON DELETE CASCADE
);

CREATE TABLE KepMegtekintes(
    felhasznalo_id NUMBER(10) NOT NULL,
    kep_id NUMBER(10) NOT NULL,
    idopont TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT kepmegtekintes_pk PRIMARY KEY(felhasznalo_id, kep_id),
    CONSTRAINT kepmegtekintes_fk_fel_id FOREIGN KEY(felhasznalo_id) REFERENCES Felhasznalo(id),
    CONSTRAINT kepmegtekintes_fk_kep_id FOREIGN KEY(kep_id) REFERENCES Kep(id) ON DELETE CASCADE
);

CREATE TABLE Bejegyzes(
    id NUMBER(10) DEFAULT bejegyzes_seq.NEXTVAL NOT NULL,
    felhasznalo_id NUMBER(10) NOT NULL,
    cim VARCHAR2(100) NOT NULL,
    szoveg VARCHAR2(1000) NOT NULL,
    idopont TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT bejegyzes_pk_id PRIMARY KEY(id),
    CONSTRAINT bejegyzes_fk_fel_id FOREIGN KEY(felhasznalo_id) REFERENCES Felhasznalo(id) ON DELETE CASCADE
);

CREATE TABLE Verseny(
    id NUMBER(10) DEFAULT verseny_seq.NEXTVAL NOT NULL,
    cim VARCHAR2(100) NOT NULL,
    szoveg VARCHAR2(1000) NOT NULL,
    szavazas_kezdete TIMESTAMP NOT NULL,
    szavazas_vege TIMESTAMP NOT NULL,
    CONSTRAINT verseny_pk_id PRIMARY KEY(id)
);

CREATE TABLE Nevezes(
    id NUMBER(10) DEFAULT nevezes_seq.NEXTVAL NOT NULL,
    verseny_id NUMBER(10) NOT NULL,
    kep_id NUMBER(10) NOT NULL,
    CONSTRAINT nevezes_pk_id PRIMARY KEY(id),
    CONSTRAINT nevezes_u_verseny_kep_id UNIQUE(verseny_id, kep_id),
    CONSTRAINT nevezes_fk_verseny_id FOREIGN KEY(verseny_id) REFERENCES Verseny(id) ON DELETE CASCADE,
    CONSTRAINT nevezes_fk_kep_id FOREIGN KEY(kep_id) REFERENCES Kep(id) ON DELETE CASCADE
);

CREATE TABLE Szavazat(
    nevezes_id NUMBER(10) NOT NULL,
    felhasznalo_id NUMBER(10) NOT NULL,
    CONSTRAINT szavazat_pk PRIMARY KEY(nevezes_id, felhasznalo_id),
    CONSTRAINT szavazat_fk_nevezes_id FOREIGN KEY(nevezes_id) REFERENCES Nevezes(id) ON DELETE CASCADE,
    CONSTRAINT szavazat_fk_fel_id FOREIGN KEY(felhasznalo_id) REFERENCES Felhasznalo(id) ON DELETE CASCADE
);

