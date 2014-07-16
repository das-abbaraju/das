--liquibase formatted sql

--changeset dalvarado:12
--preConditions onFail MARK_RAN

-- Add billableEntityId as a self-referencing FK, which points to the operator in this site's hierarchy that is the
-- billable entity for this site. Defaults to NULL which indicates that this site is it's own billable entity (the current behavior).
-- The billableEntityId must be NULL or an ancestor in this site's hierarchy.

ALTER TABLE operators
ADD COLUMN billableEntityId INT DEFAULT NULL AFTER inheritInsuranceCriteria;

ALTER TABLE operators
  ADD CONSTRAINT FK_OPER_BE_OPER FOREIGN KEY (billableEntityId) REFERENCES operators(id);
