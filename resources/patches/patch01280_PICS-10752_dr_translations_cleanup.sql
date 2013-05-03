-- Add the js column with sensible defaults. Alex said that he'd take care of updating the appropriate rows
-- manually at a later time.
ALTER TABLE `app_translation`
ADD COLUMN `js` TINYINT(1) DEFAULT 0  NOT NULL  COMMENT '1 indicates translation applies to javascript' AFTER `contentDriven`;