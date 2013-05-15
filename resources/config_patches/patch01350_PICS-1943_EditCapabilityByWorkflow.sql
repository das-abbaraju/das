ALTER TABLE `workflow`
ADD COLUMN `useStateForEdit` tinyint(4) DEFAULT 0;

ALTER TABLE `workflow_state`
ADD COLUMN `contractorCanEdit` tinyint(4) DEFAULT 0;

ALTER TABLE `workflow_state`
ADD COLUMN `operatorCanEdit` tinyint(4) DEFAULT 0;

