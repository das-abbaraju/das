--liquibase formatted sql

--changeset cfranks:5
DROP FUNCTION IF EXISTS get_parent_company;
DELIMITER $$
CREATE FUNCTION get_parent_company( account_parentID INT )
  RETURNS INT
  READS SQL DATA
BEGIN
  DECLARE max_parentID INT;

  SET max_parentID = account_parentID;

  WHILE max_parentID IS NOT NULL DO

    SELECT o.parentID INTO max_parentID
    FROM operators o
    WHERE o.id = account_parentID;

    IF  max_parentID IS NOT NULL THEN
       SET account_parentID = max_parentID;
    END IF;

  END WHILE;

  RETURN account_parentID;
END;
$$
DELIMITER ;
