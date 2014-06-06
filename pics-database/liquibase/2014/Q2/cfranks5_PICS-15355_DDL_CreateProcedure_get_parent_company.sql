--liquibase formatted SQL

--sql:
--changeset cfranks:5a splitStatements:true endDelimiter:|
DROP PROCEDURE IF EXISTS update_max_parent
;
--sql:
--changeset cfranks:5b splitStatements:false endDelimiter:|
CREATE DEFINER=`pics_admin`@`%` PROCEDURE update_max_parent ()
BEGIN
	DECLARE v_id INT;
	DECLARE v_parentID INT;
	DECLARE v_maxParentID INT;
		
	DECLARE no_more_rows BOOL DEFAULT FALSE;
	
	DECLARE get_parent_cur CURSOR FOR
		SELECT a.id, o.parentID, o.parentID AS maxParentID
		FROM operators o
		JOIN accounts a ON o.id = a.id
		WHERE a.status = 'Active'
		AND o.reportingID IS NULL
		AND o.parentID IS NOT NULL;

	DECLARE CONTINUE HANDLER FOR NOT FOUND
	    SET no_more_rows = TRUE;
	    
	OPEN get_parent_cur;

	the_loop: LOOP
		FETCH get_parent_cur
		INTO v_id, v_parentID, v_maxParentID;
		
		IF no_more_rows THEN
			CLOSE get_parent_cur;
			LEAVE the_loop;
		END IF;
		
		WHILE v_maxParentID IS NOT NULL DO

			SELECT parentID INTO v_maxParentID
			FROM operators
			WHERE id = v_parentID;
			
			IF v_maxParentID IS NOT NULL THEN
				SET v_parentID = v_maxParentID;
			END IF;
		END WHILE;
	
		UPDATE operators
		SET reportingID = v_parentID
		WHERE id = v_id;
		
	END LOOP the_loop;
	CLOSE get_parent_cur;
END$$
DELIMITER

/*
-- before run
SELECT a.id, a.name, o.reportingID, o.parentID
FROM operators o
JOIN accounts a ON o.id = a.id
WHERE a.status = 'Active'
AND o.reportingID IS NULL
AND o.parentID IS NOT NULL;

-- run
CALL update_max_parent();

-- after run
SELECT a.name, a2.name, a3.name, o.id, o.reportingID, o.parentID
FROM operators o
JOIN accounts a ON a.id = o.id
JOIN accounts a2 ON a2.id = o.parentID
JOIN accounts a3 ON a3.id = o.reportingID
WHERE o.id IN
(id1 ,id2);
*/
;
