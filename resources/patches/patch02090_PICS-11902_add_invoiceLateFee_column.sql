DROP PROCEDURE IF EXISTS ADD_COLUMN_11902;

DELIMITER //
CREATE PROCEDURE ADD_COLUMN_11902()
  BEGIN
    IF NOT EXISTS((SELECT
                     *
                   FROM information_schema.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                         AND COLUMN_NAME = 'lateFeeInvoice' AND TABLE_NAME = 'invoice'))
    THEN
      ALTER TABLE invoice ADD lateFeeInvoice INT(11);
    END IF;

  END;
//
DELIMITER ;

CALL ADD_COLUMN_11902();
DROP PROCEDURE IF EXISTS ADD_COLUMN_11902;
