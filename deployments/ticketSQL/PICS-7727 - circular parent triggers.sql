DELIMITER $$

DROP TRIGGER /*!50032 IF EXISTS */ `facilities_circular_parent_after_insert`$$

CREATE
    /*!50017 DEFINER = 'mdo'@'%' */
    TRIGGER `facilities_circular_parent_after_insert` AFTER INSERT ON `facilities` 
    FOR EACH ROW BEGIN
	if new.corporateID = new.opID then
	  SIGNAL SQLSTATE '45000'
		SET MESSAGE_TEXT = 'Circular Reference. Don''t select your own account';
	end if;
    END;
$$

DELIMITER ;

DELIMITER $$

DROP TRIGGER /*!50032 IF EXISTS */ `facilities_circular_parent_after_update`$$

CREATE
    /*!50017 DEFINER = 'mdo'@'%' */
    TRIGGER `facilities_circular_parent_after_update` AFTER UPDATE ON `facilities` 
    FOR EACH ROW BEGIN
	if new.corporateID = new.opID then
	  SIGNAL SQLSTATE '45000'
		SET MESSAGE_TEXT = 'Circular Reference. Don''t select your own account';
	end if;
    END;
$$

DELIMITER ;

DELIMITER $$

DROP TRIGGER /*!50032 IF EXISTS */ `operators_circular_parent_after_insert`$$

CREATE
    /*!50017 DEFINER = 'mdo'@'%' */
    TRIGGER `operators_circular_parent_after_insert` AFTER INSERT ON `operators` 
    FOR EACH ROW BEGIN
	if new.parentID = new.id then
	  SIGNAL SQLSTATE '45000'
		SET MESSAGE_TEXT = 'Circular Reference. Don''t select your own account';
	end if;
    END;
$$

DELIMITER ;

DELIMITER $$

DROP TRIGGER /*!50032 IF EXISTS */ `operators_circular_parent_after_update`$$

CREATE
    /*!50017 DEFINER = 'mdo'@'%' */
    TRIGGER `operators_circular_parent_after_update` AFTER UPDATE ON `operators` 
    FOR EACH ROW BEGIN
	if new.parentID = new.id then
	  SIGNAL SQLSTATE '45000'
		SET MESSAGE_TEXT = 'Circular Reference. Don''t select your own account';
	end if;
    END;
$$

DELIMITER ;