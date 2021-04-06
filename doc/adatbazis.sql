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


CREATE SEQUENCE felhasznalo_seq;
CREATE SEQUENCE kategoria_seq;
CREATE SEQUENCE kulcsszo_seq;
CREATE SEQUENCE kep_seq;
CREATE SEQUENCE bejegyzes_seq;
CREATE SEQUENCE verseny_seq;
CREATE SEQUENCE nevezes_seq;

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
    PRIMARY KEY(id),
    UNIQUE(email)
);

CREATE TABLE Kategoria(
    id NUMBER(10) DEFAULT kategoria_seq.NEXTVAL NOT NULL,
    nev VARCHAR2(50) NOT NULL,
    PRIMARY KEY(id),
    UNIQUE(nev)
);

CREATE TABLE Kulcsszo(
    id NUMBER(10) DEFAULT kulcsszo_seq.NEXTVAL NOT NULL,
    nev VARCHAR2(50) NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE Kep(
    id NUMBER(10) DEFAULT kep_seq.NEXTVAL NOT NULL,
    felhasznalo_id NUMBER(10) NOT NULL,
    kategoria_id NUMBER(10) NOT NULL,
    cim VARCHAR2(50) NOT NULL,
    tartalom CLOB NOT NULL,
    idopont TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    telepules VARCHAR2(50),
    PRIMARY KEY(id),
    FOREIGN KEY(felhasznalo_id) REFERENCES Felhasznalo(id) ON DELETE CASCADE,
    FOREIGN KEY(kategoria_id) REFERENCES Kategoria(id) ON DELETE CASCADE
);

CREATE TABLE KepKulcsszo(
    kep_id NUMBER(10) NOT NULL,
    kulcsszo_id NUMBER(10) NOT NULL,
    PRIMARY KEY(kep_id, kulcsszo_id),
    FOREIGN KEY(kep_id) REFERENCES Kep(id) ON DELETE CASCADE,
    FOREIGN KEY(kulcsszo_id) REFERENCES Kulcsszo(id) ON DELETE CASCADE
);

CREATE TABLE Ertekeles(
    felhasznalo_id NUMBER(10) NOT NULL,
    kep_id NUMBER(10) NOT NULL,
    csillagok NUMBER(1) NOT NULL,
    PRIMARY KEY(felhasznalo_id, kep_id),
    FOREIGN KEY(felhasznalo_id) REFERENCES Felhasznalo(id) ON DELETE CASCADE,
    FOREIGN KEY(kep_id) REFERENCES Kep(id) ON DELETE CASCADE
);

CREATE TABLE Komment(
    felhasznalo_id NUMBER(10) NOT NULL,
    kep_id NUMBER(10) NOT NULL,
    szoveg VARCHAR2(1000) NOT NULL,
    idopont TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(felhasznalo_id, kep_id),
    FOREIGN KEY(felhasznalo_id) REFERENCES Felhasznalo(id) ON DELETE CASCADE,
    FOREIGN KEY(kep_id) REFERENCES Kep(id) ON DELETE CASCADE
);

CREATE TABLE KepMegtekintes(
    felhasznalo_id NUMBER(10) NOT NULL,
    kep_id NUMBER(10) NOT NULL,
    idopont TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(felhasznalo_id, kep_id),
    FOREIGN KEY(felhasznalo_id) REFERENCES Felhasznalo(id),
    FOREIGN KEY(kep_id) REFERENCES Kep(id) ON DELETE CASCADE
);

CREATE TABLE Bejegyzes(
    id NUMBER(10) DEFAULT bejegyzes_seq.NEXTVAL NOT NULL,
    felhasznalo_id NUMBER(10) NOT NULL,
    cim VARCHAR2(100) NOT NULL,
    szoveg VARCHAR2(1000) NOT NULL,
    idopont TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(id),
    FOREIGN KEY(felhasznalo_id) REFERENCES Felhasznalo(id) ON DELETE CASCADE
);

CREATE TABLE Verseny(
    id NUMBER(10) DEFAULT verseny_seq.NEXTVAL NOT NULL,
    szavazas_kezdete TIMESTAMP NOT NULL,
    szavazas_vege TIMESTAMP NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE Nevezes(
    id NUMBER(10) DEFAULT nevezes_seq.NEXTVAL NOT NULL,
    verseny_id NUMBER(10) NOT NULL,
    kep_id NUMBER(10) NOT NULL,
    PRIMARY KEY(id),
    UNIQUE(verseny_id, kep_id),
    FOREIGN KEY(verseny_id) REFERENCES Verseny(id) ON DELETE CASCADE,
    FOREIGN KEY(kep_id) REFERENCES Kep(id) ON DELETE CASCADE
);

CREATE TABLE Szavazat(
    nevezes_id NUMBER(10) NOT NULL,
    felhasznalo_id NUMBER(10) NOT NULL,
    PRIMARY KEY(nevezes_id, felhasznalo_id),
    FOREIGN KEY(nevezes_id) REFERENCES Nevezes(id) ON DELETE CASCADE,
    FOREIGN KEY(felhasznalo_id) REFERENCES Felhasznalo(id) ON DELETE CASCADE
);
