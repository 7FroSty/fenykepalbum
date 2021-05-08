-- KategoriaRepository.java 32.sor
SELECT kat.id, kat.nev, COUNT(kep.id) AS kepdb
FROM Kategoria kat
LEFT JOIN Kep ON kep.kategoria_id = kat.id
GROUP BY kat.id, kat.nev
ORDER BY kat.nev ASC;

-- KommentRepository.java 49.sor
SELECT k.id, k.kep_id, k.szoveg, k.idopont,
       f.id AS felhasznalo_id, f.nev AS f_nev, f.email AS f_email
FROM Komment k
LEFT JOIN Felhasznalo f ON k.felhasznalo_id = f.id
WHERE kep_id = ?
ORDER BY k.idopont DESC;

-- VersenyRepository.java 83.sor
SELECT N.ID AS N_ID, K.*,
    (SELECT COUNT(*) FROM SZAVAZAT WHERE SZAVAZAT.NEVEZES_ID = N.ID) AS szavazat_db
FROM VERSENY
RIGHT JOIN NEVEZES N on VERSENY.ID = N.VERSENY_ID
LEFT JOIN KEP K on K.ID = N.KEP_ID
WHERE VERSENY.ID = ?;