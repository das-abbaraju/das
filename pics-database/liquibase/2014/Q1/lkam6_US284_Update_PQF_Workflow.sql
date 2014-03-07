--liquibase formatted sql

--changeset lkam:6
--preConditions onFail MARK_RAN

UPDATE workflow_step ws
SET ws.updateDate = NOW(),
ws.updatedBy = 38586,
ws.newStatus = 'Resubmit'
WHERE ws.workflowID = 4
AND ws.oldStatus = 'Resubmitted'
AND ws.newStatus = 'Incomplete';
