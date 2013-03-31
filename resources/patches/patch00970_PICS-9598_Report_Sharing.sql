ALTER TABLE `report_user`
ADD COLUMN `hidden` tinyint(1) NOT NULL DEFAULT 0;

ALTER TABLE `report`
ADD COLUMN `deleted` tinyint(1) NOT NULL DEFAULT 0;

UPDATE report SET ownerID = createdBy WHERE createdBy IS NOT NULL;
