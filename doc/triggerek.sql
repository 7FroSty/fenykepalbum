CREATE OR REPLACE TRIGGER KepFeltoltesTrigger
BEFORE INSERT
ON Kep
FOR EACH ROW
BEGIN
    :NEW.telepules := INITCAP(:NEW.telepules);
END;
/